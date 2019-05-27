package dam.com.wenave.persistence.specifications;

import dam.com.wenave.models.Route;
import dam.com.wenave.persistence.interfaces.IRealmSpecification;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RouteSpecification implements IRealmSpecification
{
    private static final String ID_FIELD = "id";
    private final String id;

    public RouteSpecification(String id)
    {
        this.id = id;
    }

    @Override
    public RealmResults<? extends RealmObject> toRealmResults(Realm realm)
    {
        if (id == null || id.isEmpty())
        {
            return realm.where(Route.class).findAll();
        }

        RealmResults<Route> results = realm.where(Route.class).equalTo(ID_FIELD, id).findAll();
        return results;
    }
}
