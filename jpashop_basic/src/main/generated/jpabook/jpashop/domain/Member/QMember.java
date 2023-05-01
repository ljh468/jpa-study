package jpabook.jpashop.domain.Member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -276597333L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final ListPath<Address, QAddress> addressHistory = this.<Address, QAddress>createList("addressHistory", Address.class, QAddress.class, PathInits.DIRECT2);

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final SetPath<String, StringPath> favoriteFoods = this.<String, StringPath>createSet("favoriteFoods", String.class, StringPath.class, PathInits.DIRECT2);

    public final QAddress homeAddress;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLocker locker;

    public final ListPath<jpabook.jpashop.domain.Product.MemberProduct, jpabook.jpashop.domain.Product.QMemberProduct> memberProducts = this.<jpabook.jpashop.domain.Product.MemberProduct, jpabook.jpashop.domain.Product.QMemberProduct>createList("memberProducts", jpabook.jpashop.domain.Product.MemberProduct.class, jpabook.jpashop.domain.Product.QMemberProduct.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<jpabook.jpashop.domain.Order.Order, jpabook.jpashop.domain.Order.QOrder> orders = this.<jpabook.jpashop.domain.Order.Order, jpabook.jpashop.domain.Order.QOrder>createList("orders", jpabook.jpashop.domain.Order.Order.class, jpabook.jpashop.domain.Order.QOrder.class, PathInits.DIRECT2);

    public final QTeam team;

    public final QAddress workAddress;

    public final QPeriod workPeriod;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.homeAddress = inits.isInitialized("homeAddress") ? new QAddress(forProperty("homeAddress")) : null;
        this.locker = inits.isInitialized("locker") ? new QLocker(forProperty("locker"), inits.get("locker")) : null;
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team")) : null;
        this.workAddress = inits.isInitialized("workAddress") ? new QAddress(forProperty("workAddress")) : null;
        this.workPeriod = inits.isInitialized("workPeriod") ? new QPeriod(forProperty("workPeriod")) : null;
    }

}

