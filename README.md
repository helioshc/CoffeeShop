# CoffeeShop
![MSA_CoffeChain_logo_3](https://user-images.githubusercontent.com/26760226/106547009-b6e1b880-654f-11eb-8c4b-4526f2200c72.jpg)

# Table of contents

- [서비스 시나리오](#서비스-시나리오)
- [체크포인트](#체크포인트)
- [분석/설계](#분석설계)
- [구현](#구현)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [Gateway 적용](#gateway-적용)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-fallback-처리)
    - [비동기식 호출 / 시간적 디커플링 / 장애격리](#비동기식-호출--시간적-디커플링--장애격리)
- [운영](#운영)
    - [Deploy / Pipeline](#Deploy--Pipeline)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출--서킷-브레이킹--장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [Config Map](#config-map)
    - [Self-healing (Liveness Probe)](#self-healing-liveness-probe)

# 서비스 시나리오

기능적 요구사항
 1. 고객이 커피를 주문한다                                         - Order
 2. 주문이 되면 주방에 주문이 전달 된다                            - Product
 3. 주문이 전달되면 창고의 재고를 확인한다.                        - Stock
 4. 재고가 충분하면 주문량만큼 차감하고 커피 제작이 시작된다       - Product
 5. 제작이 완료되면 고객에게 전달 된다(상태 변경)                  - MyPages
 5. 재고가 불충분하면 커피 제작이 시작되지 않는다                  - Stock
 6. 제작이 시작되지 않은 주문은 고객이 취소할 수 있다              - Order
 7. 주문이 취소되면 제작이 취소 된다.                              - Product
 8. 고객은 모든 진행 내역을 조회 할 수 있다                        - MyPages
 9. 재고관리자는 자재를 입고시켜 재고를 추가한다.                  - Stock

비기능적 요구사항
1. 트랜잭션
    1) 재고가 없는 주문건은 제작을 시작할 수 없다 > Sync 호출
    2) 주문이 취소되면 제작이 취소되고 주문정보에 업데이트가 되어야 한다.> SAGA, 보상 트랜젝션
    
2. 장애격리
    1) 대리점관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다.> Async (event-driven), Eventual Consistency
    2) 결제시스템이 과중되면 주문을 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다> Circuit breaker, fallback
    
3. 성능
    1) 고객이 모든 진행내역을 조회 할 수 있도록 성능을 고려하여 별도의 view로 구성한다.> CQRS


# 체크포인트

동기식 / 비동기식 SAGA 패턴으로 마이크로 서비스간 통신 구현하고 읽기 전용 Customer Center에 MyPages를 구현하여 CUD와 R을 분리
- Saga
- CQRS
- Correlation
- Req/Resp

- Gateway : gateway 를 통해 명령어를 실행

- Deploy/ Pipeline
- Circuit Breaker
- Autoscale (HPA)
- Zero-downtime deploy (Readiness Probe)
- Config Map/ Persistence Volume
- Polyglot
- Self-healing (Liveness Probe)

# 분석설계

## AS-IS 조직 (Horizontally-Aligned)

![image](https://user-images.githubusercontent.com/75309297/106628829-8c2e4900-65bd-11eb-8360-ed2df27854e0.png)

## TO-BE 조직 (Vertically-Aligned)

![image](https://user-images.githubusercontent.com/75309297/106628986-b1bb5280-65bd-11eb-9d2b-3bc6ce6d717f.png)

## 이벤트 스토밍 결과
MSAEZ로 모델링한 이벤트스토밍 결과

### 이벤트 도출

![image](https://user-images.githubusercontent.com/75309297/106629302-09f25480-65be-11eb-8365-06896c609d26.png)

### 부적격 이벤트 탈락

![image](https://user-images.githubusercontent.com/75309297/106629404-25f5f600-65be-11eb-8e00-1751e9c3e49b.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
    - 본사에 재고요청됨, 결제됨, 결제취소됨 : 프로젝트 범위외의 서비스
    
### 엑터,커맨드 만들기

![image](https://user-images.githubusercontent.com/75309297/106637613-4629b300-65c6-11eb-9793-3b325c7cfb6e.png)

### 어그리게잇 추가

![image](https://user-images.githubusercontent.com/75309297/106637649-517cde80-65c6-11eb-8f33-fbc3931323d2.png)

    - 주문, 생산, 재고 어그리게잇을 생성하고 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨택스트 묶기

![image](https://user-images.githubusercontent.com/75309297/106637691-5f326400-65c6-11eb-8e4e-8fa9e1d3faec.png)

- 도메인 서열 분리

        - Core Domain:  cafe, kitchen, warehouse : 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 order 의 경우 1주일 1회 미만, product 의 경우 1개월 1회 미만
        - Supporting Domain:  customercenter(view) : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 90% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain: 결제서비스 등 3rd Party 서비스를 사용 시 효율이 높으나 현재는 연계서비스로 고려되지 않았음. 

### 폴리시 부착 (괄호는 수행주체, 폴리시 부착을 둘째단계에서 해놔도 상관 없음. 전체 연계가 초기에 드러남)

![image](https://user-images.githubusercontent.com/75309297/106637724-68233580-65c6-11eb-9c53-75f98f3c3ae9.png)

### 폴리시 이동

![image](https://user-images.githubusercontent.com/75309297/106637759-72453400-65c6-11eb-8df4-d5c573996e92.png)

### 컨택스트맵핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![image](https://user-images.githubusercontent.com/64818523/106861665-66ae5600-6709-11eb-9dc5-47e97a9c2df2.png)

### 완성된 모형

![image](https://user-images.githubusercontent.com/64818523/106853403-fef20e00-66fc-11eb-8b74-9f52ebc3d944.png)

    - View Model 추가

### 헥사고날 아키텍처 다이어그램 도출 (Polyglot)

![image](https://user-images.githubusercontent.com/64818523/106862184-1a174a80-670a-11eb-9368-3b9c48824976.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐

### 기능적 요구사항 검증

![image](https://user-images.githubusercontent.com/64818523/106855071-b25c0200-66ff-11eb-941c-571ff3aac2db.png)

    - 고객이 커피를 주문한다                                         (OK)
    - 주문이 되면 주방에 주문이 전달 된다                            (OK)
    - 주문이 전달되면 창고의 재고를 확인한다.                        (OK)
    - 재고가 충분하면 주문량만큼 차감하고 커피 제작이 시작된다       (OK)
    - 제작이 완료되면 고객이 order에서 조회 할 수 있다.              (OK)
    
![image](https://user-images.githubusercontent.com/64818523/106855900-074c4800-6701-11eb-8d95-3abca4c483c9.png)

    - 재고가 불충분하면 커피 제작이 시작되지 않는다                  (OK)
    - 제작이 시작되지 않은 주문은 고객이 취소할 수 있다.             (OK)
    - 주문이 취소되면 제작이 취소된다.                               (OK)

![image](https://user-images.githubusercontent.com/64818523/106855955-1a5f1800-6701-11eb-8324-c888e0a3f2fe.png)

    - 재고관리자는 자재를 입고시켜 재고를 추가한다.                  (OK)
    
![image](https://user-images.githubusercontent.com/64818523/106855989-2945ca80-6701-11eb-8163-cba40db5002b.png)

    - 고객이 MyPage에서 커피주문 내역을 볼 수 있어야 한다.           (OK)
       
### 비기능 요구사항

    - 재고가 없는 주문건은 제작을 시작할 수 없다 > Sync 호출
    - 제작이 시작되지 않은 주문이 취소되면 제작이 취소되고 주문정보에도 업데이트가 되어야 한다.> SAGA, 보상 트랜젝션
    - 고객이 모든 진행내역을 조회 할 수 있도록 성능을 고려하여 별도의 view로 구성한다.> CQRS

# 구현
서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084 이다)

```
namespace 명은 cafe로 만들었다.

cd cafe
mvn package -Dmaven.test.skip=true

cd ../kitchen
mvn package -Dmaven.test.skip=true

cd ../warehouse
mvn package -Dmaven.test.skip=true

cd ../customercenter
mvn package -Dmaven.test.skip=true
```

## DDD 의 적용

각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 order 마이크로 서비스). 
이때 가능한 현업에서 사용하는 언어(유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 
하지만, 일부 구현 단계에 영문이 아닌 경우는 실행이 불가능한 경우가 발생하여 영문으로 구축하였다.  
(Maven pom.xml, Kafka의 topic id, FeignClient 의 서비스 ID 등은 한글로 식별자를 사용하는 경우 오류 발생)

![image](https://user-images.githubusercontent.com/64818523/106856871-83935b00-6702-11eb-841a-363599185a5b.png)

Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 
데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

![image](https://user-images.githubusercontent.com/64818523/106977984-af641e80-679e-11eb-9492-583aee7f7413.png)

## 폴리글랏 퍼시스턴스
warehouse MSA의 경우 H2 DB인 주문과 제작와 달리 Hsql으로 구현하여 MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다. 


cafe , kitchen, customercenter의 pom.xml 설정

![image](https://user-images.githubusercontent.com/64818523/106857746-ec2f0780-6703-11eb-9322-954e48d6ef50.png)

warehouse의 pom.xml 설정

![image](https://user-images.githubusercontent.com/64818523/106857704-d91c3780-6703-11eb-87bb-168ebeec3ba5.png)


## Gateway 적용

gateway > resources > applitcation.yml 설정

![image](https://user-images.githubusercontent.com/64818523/106858088-7d9e7980-6704-11eb-911e-5e2677002d58.png)

gateway 테스트

```
http POST http://10.0.144.217:8080/orders productName="Americano" qty=1
```
![6_Gateway](https://user-images.githubusercontent.com/77084784/106618857-4b313700-65b3-11eb-83aa-c9f04a28683b.jpg)


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 제품(product) -> 제고(stock) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 서비스를 호출하기 위하여 FeignClient 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 
``` 
// coffeeshop.external > StockService.java

package coffeeshop.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="warehouse", url="http://warehouse:8080")
public interface StockService {

    @RequestMapping(method= RequestMethod.PATCH, path="/stocks/reduce")
    public Boolean reduce(@RequestBody Stock stock);

}

// 주문에 의해 재고 출고 (재고가 없으면 제작이 중지됨)
 @RequestMapping(method=RequestMethod.PATCH, path="/stocks/reduce")
 public Boolean stockReduced(@RequestBody Stock inputStock) {
     try {
             Thread.sleep((long) (1000 * 6));
        	} catch (InterruptedException e) {
             e.printStackTrace();
	    }

       Optional<Stock> stockOptional = stockRepository.findByProductName(inputStock.getProductName());

	if (stockOptional.isPresent()) {
    	Stock stock = stockOptional.get();

	    // 주문 숫자가 재고 숫자보다 클 때(재고 부족) false을 리턴
	    if(stock.getQty() < inputStock.getQty() ) {
	 	return false;
	    } else {  
            // 재고 차감 후 true을 리턴한다.
	        stock.setQty( stock.getQty() - inputStock.getQty() );
                stockRepository.save(stock);

                return true;
	        }
	} else {
	      // 재고 목록에 없을 때 false을 리턴
	      return false;
	}
    }


```
- 주문 취소 시 제작을 먼저 취소하도록 구현 (주문 취소중 제작이 시작되면 주문 취소가 안됨)
```
// (cafe) Order.java (Entity)

    @PreUpdate
    public void onPreUpdate(){

        // 
        if (this.getStatus().equals("OrderCanceled")) {

            // Event 
            OrderCanceled orderCanceled = new OrderCanceled();

            // Aggregate 
            BeanUtils.copyProperties(this, orderCanceled);

            coffeeshop.external.Product product = new coffeeshop.external.Product();
            product.setId(orderCanceled.getProductId());
            product.setOrderId(orderCanceled.getId());
            product.setProductName(orderCanceled.getProductName());
            product.setStatus(orderCanceled.getStatus());
            product.setQty(orderCanceled.getQty());

            // req/res
            CafeApplication.applicationContext.getBean(coffeeshop.external.ProductService.class)
                .cancel( product );
        }

![8_Req_Res](https://user-images.githubusercontent.com/77084784/106619124-99463a80-65b3-11eb-827d-bae3d43ccfe7.jpg)

- 동기식 호출이 적용되서 재고 서비스에 장애가 나면 제작 서비스도 못받는다는 것을 확인:

```
#재고(stock) 서비스를 잠시 내려놓음 (ctrl+c)

#주문 (order)
http PATCH http://localhost:8081/orders/1 status="Canceled"    #Fail
```
![9_cancel_fail](https://user-images.githubusercontent.com/77084784/106677389-067dbe00-65fc-11eb-8309-12ba029321d9.jpg)

```
#재고(stock) 서비스 재기동
cd warehouse
mvn spring-boot:run

#주문 (order) -> 제작 (product)
http PATCH http://localhost:8081/orders/2 status="Canceled"    #Success
```
![9_cancel_ok](https://user-images.githubusercontent.com/77084784/106677460-1eedd880-65fc-11eb-8470-4b8c0b170c8f.jpg)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 


주문(order)이 이루어진 후에 제품(product)로 이를 알려주는 행위는 비 동기식으로 처리하여 제작(product)의 처리를 위하여 주문이 블로킹 되지 않아도록 처리한다.
 
- 주문이 되었다(Ordered)는 도메인 이벤트를 카프카로 송출한다(Publish)
 
![10_비동기 호출(주문_제조)](https://user-images.githubusercontent.com/77084784/106619371-e0343000-65b3-11eb-9599-ca40b275751b.jpg)

- 제품(product)에서는 주문(ordered) 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다.
- 주문접수(Order)는 송출된 주문완료(ordered) 정보를 제품(product)의 Repository에 저장한다.:
 
![11_비동기 호출(주문_제조)](https://user-images.githubusercontent.com/77084784/106619501-01951c00-65b4-11eb-88e9-8870bad805f7.jpg)

제품(product) 시스템은 주문(order)/제고(stock)와 완전히 분리되어있으며(sync transaction 없음), 이벤트 수신에 따라 처리되기 때문에, 제품(product)이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다.(시간적 디커플링):
```bash
#제품(product) 서비스를 잠시 내려놓음 (ctrl+c)

#주문하기(order)
http http://localhost:8081/orders item="Americano" qty=1  #Success

#주문상태 확인
http GET http://localhost:8081/orders/1    # 상태값이 'Completed'이 아닌 'Requested'에서 멈춤을 확인
```
![12_time분리_1](https://user-images.githubusercontent.com/77084784/106619595-196ca000-65b4-11eb-892e-a0ad2fa1b7f0.jpg)
```bash
#제품(product) 서비스 기동
cd product
mvn spring-boot:run

#주문상태 확인
http GET http://localhost:8081/orders/1     # 'Requested' 였던 상태값이 'Completed'로 변경된 것을 확인
```
![12_time분리_2](https://user-images.githubusercontent.com/77084784/106619700-330de780-65b4-11eb-818e-70152aba4400.jpg)

# 운영

## Deploy / Pipeline
- 네임스페이스 만들기
```
kubectl create ns cafe
kubectl get ns
```
![kubectl_create_ns](https://user-images.githubusercontent.com/26760226/106624530-1922d380-65b9-11eb-916a-5b6956a013ad.png)


- 소스 가져오기
```
git clone https://github.com/helioshc/CoffeeShop.git
```
![git_clone](https://user-images.githubusercontent.com/26760226/106623315-d6143080-65b7-11eb-8bf0-b7604d2dd2db.png)

- 빌드 하기
```
배포 순서 

1. gateway

cd gateway
mvn package -Dmaven.test.skip=true
az acr build --registry helioshc --image helioshc.azurecr.io/gateway:latest .
kubectl create deploy gateway --image=helioshc.azurecr.io/gateway:latest -n cafe
kubectl expose deploy gateway --type="ClusterIP" --port=8080 -n cafe

2. warehouse (kitchen의 ConfigMap 생성을 위해 warehouse를 먼저 배포한다)
# cafe, kitchen은 ConfigMap 항목을 참조하기 때문에 ConfigMap 이 먼저 생성되어 있지 않으면 containerConfigErr 오류가 발생한다.
cd ../wa*
mvn package -Dmaven.test.skip=true
az acr build --registry helioshc --image helioshc.azurecr.io/warehouse:latest .
kubectl apply -f kubernetes/deployment.yml
kubectl expose deploy warehouse --type="ClusterIP" --port=8080 -n cafe

3. kitchen (cafe의 ConfigMap 생성을 위해 kitchen을 먼저 배포한다)


- 설치전 warehouse의 ClusterIP 정보 확인
kubectl get all -n cafe

- configmap에 warehouse의 ClusterIP 지정하여 생성
kubectl delete configmap apikitchenurl -n cafe
kubectl create configmap apikitchenurl --from-literal=url=http://10.0.174.132:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n cafe

cd ../ki*
mvn package -Dmaven.test.skip=true
az acr build --registry helioshc --image helioshc.azurecr.io/kitchen:latest .
kubectl apply -f kubernetes/deployment.yml
kubectl expose deploy kitchen --type="ClusterIP" --port=8080 -n cafe

# cafe

cd ..
cd ca*

- 설치전 kitchen ClusterIP 정보 확인
kubectl get all -n cafe

- configmap에 kitchen ClusterIP 지정하여 생성
kubectl delete configmap apicafeurl -n cafe
kubectl create configmap apicafeurl    --from-literal=url=http://10.0.221.109:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n cafe

mvn package -Dmaven.test.skip=true        
az acr build --registry helioshc --image helioshc.azurecr.io/cafe:latest .
kubectl apply -f kubernetes/deployment.yml 
kubectl expose deploy cafe --type="ClusterIP" --port=8080 -n cafe


# customercenter

cd ..
cd cu*
mvn package -Dmaven.test.skip=true
az acr build --registry helioshc --image helioshc.azurecr.io/customercenter:latest .
kubectl apply -f kubernetes/deployment.yml
kubectl expose deploy customercenter --type="ClusterIP" --port=8080 -n cafe

# 확인 예시
kubectl exec -it httpie -- bin/bash
http GET http://10.0.246.207:8080/orders
http GET http://10.0.89.75:8080/orders

# kitchen
cd ..
cd ki*
mvn package -Dmaven.test.skip=true
az acr build --registry helioshc --image helioshc.azurecr.io/kitchen:latest .
kubectl apply -f kubernetes/deployment.yml
kubectl expose deploy kitchen --type="ClusterIP" --port=8080 -n cafe

# warehouse
cd ..
cd wa*
mvn package -Dmaven.test.skip=true
az acr build --registry helioshc --image helioshc.azurecr.io/warehouse:latest .
kubectl apply -f kubernetes/deployment.yml
kubectl expose deploy warehouse --type="ClusterIP" --port=8080 -n cafe


## 동기식 호출 / 서킷 브레이킹 / 장애격리
* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 제작 (product)--> 재고 (stock) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 재고 사용 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정: 요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

* siege 툴 사용법:
```
 siege가 생성되어 있지 않으면:
 kubectl run siege --image=apexacme/siege-nginx -n cafe
 siege 들어가기:
 kubectl exec -it pod/siege-5c7c46b788-4rn4r -c siege -n cafe -- /bin/bash
 siege 종료:
 Ctrl + C -> exit
```
* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
siege -c10 -t60S -r10 -v --content-type "application/json" 'http://10.0.209.210:8080/products POST {"orderId":1, "status":"Requested", "productName":"Ame", "qty":1}'
```
- 부하 발생하여 CB가 발동하여 요청 실패처리하였고, 밀린 부하가 stock에서 처리되면서 다시 product를 받기 시작

![image](https://user-images.githubusercontent.com/6468351/106703226-31ccd100-662d-11eb-9463-a10bb211cd70.png)

- report

![image](https://user-images.githubusercontent.com/6468351/106702534-da7a3100-662b-11eb-99f8-b54962eff735.png)

- CB 잘 적용됨을 확인

## 오토스케일 아웃

## 무정지 재배포
- 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscale 이나 CB 설정을 제거함
- seige 로 배포작업 직전에 워크로드를 모니터링 함
```bash
kubectl apply -f kubernetes/deployment_readiness.yml
```
- readiness 옵션이 없는 경우 배포 중 서비스 요청처리 실패 <br>
![1](https://user-images.githubusercontent.com/26760226/106704039-bec45a00-662e-11eb-9a26-dc5d0c403d03.png)

- deployment.yml에 readiness 옵션을 추가 <br>
![2](https://user-images.githubusercontent.com/26760226/106704044-bff58700-662e-11eb-8842-4d1bbbead1ef.png)

- readiness적용된 deployment.yml 적용
```bash
kubectl apply -f kubernetes/deployment.yml
```
- 새로운 버전의 이미지로 교체
```bash
az acr build --registry skccteam03 --image skccteam03.azurecr.io/customercenter:v1 .
kubectl set image deploy customercenter customercenter=skccteam03.azurecr.io/customercenter:v1 -n cafe
```

- 기존 버전과 새 버전의 store pod 공존 중 <br>
![3](https://user-images.githubusercontent.com/26760226/106704049-bff58700-662e-11eb-8199-a20723c5245d.png)

- Availability: 100.00 % 확인 <br>
![4](https://user-images.githubusercontent.com/26760226/106704050-c08e1d80-662e-11eb-9214-9136748e1336.png)

## Config Map

### Service ClusterIP 확인
![image](https://user-images.githubusercontent.com/64818523/106609778-4c5d6680-65a9-11eb-8b31-8e11b3e22162.png)

### order ConfigMap 설정
- order/src/main/resources/apllication.yml 설정

- default쪽

![image](https://user-images.githubusercontent.com/64818523/106609096-8ed27380-65a8-11eb-88a2-e1b732e17869.png)

- docker쪽

![image](https://user-images.githubusercontent.com/64818523/106609301-c7724d00-65a8-11eb-87d3-d6f03c693db6.png)

- order/kubernetes/Deployment.yml 설정

![image](https://user-images.githubusercontent.com/64818523/106609409-dd800d80-65a8-11eb-8321-aa047e8a68aa.png)


### product ConfigMap 설정
- product/src/main/resources/apllication.yml 설정

- default쪽
  
![image](https://user-images.githubusercontent.com/64818523/106609502-f8eb1880-65a8-11eb-96ed-8eeb1fc9f87c.png)

- docker쪽
    
![image](https://user-images.githubusercontent.com/64818523/106609558-0bfde880-65a9-11eb-9b5a-240566adbad1.png)

- product/kubernetes/Deployment.yml 설정

![image](https://user-images.githubusercontent.com/64818523/106612752-c93e0f80-65ac-11eb-9509-9938f4ccf767.png)


### config map 생성 후 조회
```
kubectl create configmap apiorderurl --from-literal=url=http://10.0.54.30:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n cafe
```
![image](https://user-images.githubusercontent.com/64818523/106609630-1f10b880-65a9-11eb-9c1d-be9d65f03a1e.png)

```
kubectl create configmap apiproducturl --from-literal=url=http://10.0.164.216:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n cafe
```
![image](https://user-images.githubusercontent.com/64818523/106609694-3485e280-65a9-11eb-9b59-c0d4a2ba3aed.png)

- 설정한 url로 주문 호출
```
http POST localhost:8081/orders productName="Americano" qty=1
```
![image](https://user-images.githubusercontent.com/73699193/98109319-b732cf00-1ee0-11eb-9e92-ad0e26e398ec.png)

- configmap 삭제 후 app 서비스 재시작
```
kubectl delete configmap apicafeurl -n cafe
kubectl delete configmap apikitchenurl -n cafe

kubectl get pod/order-74c76b478-bvgrr -n cafe -o yaml | kubectl replace --force -f-
kubectl get pod/product-66ddb989b8-9j46l -n cafe -o yaml | kubectl replace --force -f-
```

![image](https://user-images.githubusercontent.com/64818523/106710293-0c45c480-6639-11eb-8512-94b5009d34cf.png)

- configmap 삭제된 상태에서 주문 호출  
```
kubectl exec -it httpie -- /bin/bash
http POST http://10.0.101.221:8080/orders productName="Tea" qty=3
```
![image](https://user-images.githubusercontent.com/64818523/106706737-765b6b00-6633-11eb-9e73-48aa1190acdb.png)

- configmap 삭제된 상태에서 Pod와 deploy 상태 확인
```
kubectl get all -n cafe
```
![image](https://user-images.githubusercontent.com/64818523/106706899-b4588f00-6633-11eb-9670-169421b045ed.png)

- Pod와 상태 상세 확인
```
kubectl get pod order-74c76b478-mlpf4 -o yaml -n cafe
```
![image](https://user-images.githubusercontent.com/64818523/106706929-c33f4180-6633-11eb-843c-535c0b37904d.png)



## Self-healing (Liveness Probe)

- product 서비스 정상 확인
```
kubectl get all -n cafe
```

![image](https://user-images.githubusercontent.com/27958588/98096336-fb1cd880-1ece-11eb-9b99-3d704cd55fd2.jpg)


- deployment.yml 에 Liveness Probe 옵션 추가
```
cd ~/coffee/product/kubernetes
vi deployment.yml

(아래 설정 변경)
livenessProbe:
	tcpSocket:
	  port: 8081
	initialDelaySeconds: 5
	periodSeconds: 5
```
![image](https://user-images.githubusercontent.com/27958588/98096375-0839c780-1ecf-11eb-85fb-00e8252aa84a.jpg)

- product pod에 liveness가 적용된 부분 확인
```
kubectl describe deploy product -n cafe
```
![image](https://user-images.githubusercontent.com/27958588/98096393-0a9c2180-1ecf-11eb-8ac5-f6048160961d.jpg)

- product 서비스의 liveness가 발동되어 13번 retry 시도 한 부분 확인
```
kubectl get pod -n cafe
```

![image](https://user-images.githubusercontent.com/27958588/98096461-20a9e200-1ecf-11eb-8b02-364162baa355.jpg)

