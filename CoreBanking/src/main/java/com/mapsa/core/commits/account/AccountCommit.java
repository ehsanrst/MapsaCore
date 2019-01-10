package com.mapsa.core.commits.account;

import com.mapsa.core.AccountIMD;
import com.mapsa.core.commits.Commit;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public abstract class AccountCommit extends Commit implements Serializable {

    public void apply(AccountIMD IMD){
    }

}
