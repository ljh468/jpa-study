package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;

  /**
   * - V1. 엔티티 직접 노출
   * - Hibernate5Module 모듈 등록, LAZY=null 처리
   * - 양방향 관계 문제 발생 -> @JsonIgnore
   */
  @GetMapping("/v1/simple-orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAllByString(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName(); // Lazy 강제 초기화
      order.getDelivery().getAddress(); // Lazy 강제 초기화
    }
    return all;
  }

  /**
   * - V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
   * - 단점: 지연로딩으로 쿼리 N번 호출
   */
  @GetMapping("/v2/simple-orders")
  public List<SimpleOrderDto> ordersV2() {
    // ORDER 2개
    // N + 1 -> 1 + 회원 N + 배송 N
    List<Order> orders = orderRepository.findAllByString(new OrderSearch());
    List<SimpleOrderDto> result = orders.stream()
                                        .map(SimpleOrderDto::new)
                                        .collect(toList());
    return result;
  }

  /**
   * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
   * - fetch join으로 쿼리 1번 호출
   * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
   */
  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> ordersV3() {
    return orderRepository.findAllWithMemberDelivery().stream()
                          .map(SimpleOrderDto::new)
                          .collect(toList());
  }

  /**
   * V4. JPA 에서 DTO 로 바로 조회
   * - 쿼리 1번 호출
   * - select 절에서 원하는 데이터만 선택해서 조회
   */
  @GetMapping("/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> orderV4() {
    return orderRepository.findOrderDtos();
  }

  /**
   * 쿼리 방식 선택 권장 순서
   * 1. 우선 엔티티를 DTO 로 변환하는 방법을 선택한다.
   * 2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
   * 3. 그래도 안되면 DTO 로 직접 조회하는 방법을 사용한다.
   * 4. 최후의 방법은 JPA 가 제공하는 네이티브 SQL 이나 스프링 JDBC Template 을 사용해서 SQL 을 직접
   * 사용한다.
   */

  @Data
  static class SimpleOrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; // 주문시간
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName(); // LAZY 초기화
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress(); // LAZY 초기화
    }
  }

}
