package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

  @PersistenceContext // @Autowired 도 가능
  private EntityManager em;

  public void save(Member member) {
    em.persist(member);
  }

  public Member findOne(Long id) {
    return em.find(Member.class, id);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class)
             .getResultList();
  }

  public List<Member> findByName(String name) {
    return em.createQuery("select m from Member m where m.name = :name", Member.class)
             .setParameter("name", name)
             .getResultList();
  }

  // limit 방식
  public boolean existsByNameWithLimit(String name) {
    return em.createQuery("select m.id from Member m where m.name = :name", Long.class)
             .setParameter("name", name)
             .setMaxResults(1)
             .getResultList()
             .size() > 0;
  }

  // count 방식
  public boolean existsByNameWithCount(String name) {
    return em.createQuery("select count(m) from Member m where m.name = :name", Long.class)
             .setParameter("name", name)
             .getSingleResult() > 0;
  }
}
