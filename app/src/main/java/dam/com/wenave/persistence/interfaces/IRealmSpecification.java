package dam.com.wenave.persistence.interfaces;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface IRealmSpecification
{
    RealmResults<? extends RealmObject> toRealmResults(Realm realm);
}
