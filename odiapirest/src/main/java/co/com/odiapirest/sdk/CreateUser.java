package co.com.odiapirest.sdk;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.IOdiEntityManager;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.domain.security.OdiUser;
import oracle.odi.domain.security.OdiUserCreationServiceImpl;
import oracle.odi.domain.security.OdiProfile;


import java.util.Date;

public class CreateUser {

    String url = "jdbc:oracle:thin:@lnxdbdevhw:1810/ODIDEVMT"; /*Master Repository: JDBC URL */
    String driver = "oracle.jdbc.OracleDriver"; /*Master Repository: JDBC driver */
    String schema = "MRDEV_DWH"; /*Master Repository: Database user for schema access*/
    String schemapwd = "ODIDEVMT18."; /*Master Repository JDBC URL */
    String workrep = "WRDEV_DESARROLLO_SEMANTIX"; /*Name of the Work Repository */
    String odiuser= "ADMINSPA"; /* ODI User name used to connect to the repositories */
    String odiuserpwd = "DEVSPA2019*"; /* ODI User password to connect to the repositories */



    public void CrearUsuarioOdi(String user, String pwd, boolean supervisor, Date expirationDate){
        MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(url, driver, schema, schemapwd.toCharArray(), new PoolingAttributes());
        WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(workrep, new PoolingAttributes());
        OdiInstance odiInstance = OdiInstance.createInstance(new OdiInstanceConfig(masterInfo, workInfo));

        Authentication auth = odiInstance.getSecurityManager().createAuthentication(odiuser, odiuserpwd.toCharArray());
        odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
        DefaultTransactionDefinition txnDef = new DefaultTransactionDefinition();
        ITransactionManager tm = odiInstance.getTransactionManager();
        IOdiEntityManager tme = odiInstance.getTransactionalEntityManager();
        ITransactionStatus txnStatus = tm.getTransaction(txnDef);

        OdiUserCreationServiceImpl service = new OdiUserCreationServiceImpl(odiInstance);

        OdiUser newUser = service.createOdiUser(user, pwd.toCharArray(), supervisor, expirationDate );

        OdiProfile DEV_DWH_CONECTAR = (OdiProfile)odiInstance.getTransactionalEntityManager().getFinder(OdiProfile.class).findByGlobalId("5e5d03fe-fe18-46c4-8bb0-592f000bf7b3") ;
        OdiProfile DEV_DWH_DESARROLLO = (OdiProfile)odiInstance.getTransactionalEntityManager().getFinder(OdiProfile.class).findByGlobalId("2bc5c995-4041-4323-a82e-02ee4193f2b9") ;
        OdiProfile DEV_DWH_LIDER_TECNICO_ALL_PROYECTO = (OdiProfile)odiInstance.getTransactionalEntityManager().getFinder(OdiProfile.class).findByGlobalId("5d65431d-3fed-4bdc-aa18-716e8e35b55e") ;

        newUser.addOdiProfile(DEV_DWH_CONECTAR);
        newUser.addOdiProfile(DEV_DWH_DESARROLLO);
        newUser.addOdiProfile(DEV_DWH_LIDER_TECNICO_ALL_PROYECTO);

        tm.commit(txnStatus);

    }
}
