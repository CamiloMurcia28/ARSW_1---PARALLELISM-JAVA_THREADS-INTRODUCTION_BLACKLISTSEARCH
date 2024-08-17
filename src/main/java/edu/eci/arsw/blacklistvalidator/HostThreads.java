package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

// valide si en un rango a,b hay 5 ocurrencias

public class HostThreads extends Thread{

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    int a;
    int b;
    String ipaddress;
    List<Integer> blackListOcurrences;

    public HostThreads(int a, int b, String ipaddress, List<Integer> blackListOcurrences){
        this.a = a;
        this.b = b;
        this.ipaddress = ipaddress;
        this.blackListOcurrences = new LinkedList<>();
    }

    @Override
    public void run(){

                
        int ocurrencesCount=0;
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int checkedListsCount=0;
        

        for (int i=a;i<b && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            
            if (skds.isInBlackListServer(i, ipaddress)){
                
                blackListOcurrences.add(i);
                
                ocurrencesCount++;
            }
        }
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        
    }

    public List<Integer> getList(){
        return blackListOcurrences;
    }


}