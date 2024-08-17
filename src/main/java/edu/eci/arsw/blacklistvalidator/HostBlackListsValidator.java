/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    List<List<Integer>> matrixBlackList = new ArrayList<>(); 


     public static int[] dividirEnPartes(int m, int n) {
        int[] partes = new int[n];
        int parteBasica = m / n;
        int resto = m % n;

        if (m % 2 == 0) { // Si el número es par
            Arrays.fill(partes, parteBasica);
            for (int i = 0; i < resto; i++) {
                partes[i]++;
            }
        } else { // Si el número es impar
            Arrays.fill(partes, parteBasica);
            for (int i = 0; i < resto; i++) {
                partes[i]++;
            }
        }

        return partes;
    }
    public List<Integer> checkHost(String ipaddress, int n){
        
        List<Integer> blackListOccurrences = new ArrayList<>();
        int r = 0;
        List<Integer> empty = new ArrayList<>();

        
        int[] partes = dividirEnPartes(skds.getRegisteredServersCount(), n);
        //crear los n hilos
        HostThreads[] threads = new HostThreads[n];

        //usar este for para crear los hilos
        for(int i=0;i< n;i++){
            threads[i] = new HostThreads(r,r + partes[i] , ipaddress, empty);
            r = r + partes[i];
        }
        //inicia cada hilo y obtiene las subpartes 
        for(int i=0;i< n;i++){
            threads[i].start();
            List<Integer> blackList = threads[i].getList();
            matrixBlackList.add(blackList);
        }

         // Esperar que todos los hilos terminen
         for (int i = 0; i < n; i++) {
            try {
                threads[i].join();  // Esperar a que el hilo termine
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Recoger resultados después de que los hilos hayan terminado
        for (int i = 0; i < n; i++) {
            blackListOccurrences.addAll(threads[i].getList());
        }

        return blackListOccurrences;
    }
}


