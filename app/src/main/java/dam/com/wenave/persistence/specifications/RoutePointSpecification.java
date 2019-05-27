package dam.com.wenave.persistence.specifications;

import dam.com.wenave.models.RoutePoint;
import dam.com.wenave.persistence.interfaces.IRealmSpecification;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RoutePointSpecification implements IRealmSpecification
{
    private static final String FIRST_NAME_FIELD = "routeId";
    private final String routeId;

    public RoutePointSpecification(String routeId) {
        this.routeId = routeId;
    }

    public RealmResults<? extends RealmObject> toRealmResults(Realm realm)
    {
        return realm.where(RoutePoint.class)
                .contains(FIRST_NAME_FIELD, routeId)
                .findAll();
    }
}
