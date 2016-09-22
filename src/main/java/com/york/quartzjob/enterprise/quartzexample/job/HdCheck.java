package com.york.quartzjob.enterprise.quartzexample.job;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HdCheck 
{
    static String eamilAccount="";
    static String password="";
    
    public static List<String> getAllDrivers(){
        List<String> drivers= new ArrayList<String>();
        
           File[] roots = File.listRoots();
        for(int i = 0; i < roots.length ; i++) {
            String drive=roots[i].getPath();
            if (drive.endsWith("\\")) drive=drive.substring(0,drive.length()-1);
            drivers.add(drive);
         }
        
        return drivers;
    }
    
    public static void main( String[] args )
    { 
        List<String> drivers=getAllDrivers();
        
        Properties props = new Properties();
        String properties="hdmonitor.properties";
        File f= new File(properties);
        if ( !f.exists() ) {
            System.out.println("Error: mising hdmonitor.properties\n");
            return;
        }
        
        try{            
            props.load(new FileInputStream(properties));
        }catch(Exception e) {
            System.out.println("Error: can't load hdmonitor.properties\n");
            return;
        }
        
        for(String drive:drivers) {
            checkDriveFreeSpace(drive,props);
        }
    }
    
    public static long toLong(String space) {
        if (space==null ) return 0L;
        space=space.toUpperCase();
        space=space.replace("KB", "K");
        space=space.replace("MB", "M");
        space=space.replace("GB", "G");
        space=space.replace("TB", "T");
        String min=space.substring(0,space.length()-1);
        if (space.endsWith("K")) 
            return Long.parseLong(min) * 1024;
        if (space.endsWith("M")) 
            return Long.parseLong(min) * 1024 * 1024;
        if (space.endsWith("G")) 
            return Long.parseLong(min) * 1024 * 1024 * 1024;
        if (space.endsWith("T")) 
            return Long.parseLong(min) * 1024 * 1024 * 1024 * 1024;
        
        return -1L;
    }

    
    public static long getFreeSpace(String drive) {
    
        try{
            if (drive!=null && drive.length()==2) {
                File file = new File(drive);
                long totalSpace = file.getUsableSpace(); //total disk space in bytes.
                return totalSpace;
            }
        }catch(Exception e) {
            
        }
        return 0L;
    }
    
    public static String formatMinHDSpace(String space, Long freeSpace) {
        space=space.toUpperCase();
        space=space.replace("KB", "K");
        space=space.replace("MB", "M");
        space=space.replace("GB", "G");
        space=space.replace("TB", "T");
        
        if (space.endsWith("K")) 
            return Integer.toString((int)(freeSpace/1024)) + "K";
        if (space.endsWith("M")) 
            return Integer.toString((int)(freeSpace/(1024*1024))) + "M";
        if (space.endsWith("G")) 
            return Integer.toString((int)(freeSpace/(1024*1024*1024))) + "G";
        if (space.endsWith("T")) 
            return Integer.toString((int)(freeSpace/(1024*1024*1024*1024))) + "T";
        return freeSpace.toString();
    }
    
    public static boolean checkDriveFreeSpace(String drive,Properties props ) {
        if (drive==null || props==null) return true;
        if (drive.trim().equals("")) return true;
           String min=props.getProperty(drive.toUpperCase());
           if (min==null || min.equals("")) {
               min=props.getProperty(drive.toLowerCase());
           }
           //System.out.println("Minimum=" + min + " for " + drive + "\n" );
        if (min==null || min.equals("")) return true;
       
        String host=props.getProperty("mail.smtp.host", "smtp.gmail.com");
        String port=props.getProperty("mail.smtp.port", "464");
        String auth=props.getProperty("mail.smtp.auth", "true");
        String from=props.getProperty("mail.from", "freehdmonitor@gmail.com");
        eamilAccount=props.getProperty("mail.smtp.account", "freehdmonitor@gmail.com");
        password=props.getProperty("mail.smtp.password", "xxxxxx");
        String to=props.getProperty("mail.to", "");
        String cc=props.getProperty("mail.cc", "");

        String errorTitle=props.getProperty("mail.error.title", "Error: Hard Disk Free Space isn't enough");
        String warningTitle=props.getProperty("mail.warning.title", "Warning: Hard Disk Free Space isn't enough");
         long lMin=toLong(min);
        long lFreeSpace=getFreeSpace(drive);
        System.out.println("Checked " + drive + ", Minimum=" + min + "; useable space=" + lFreeSpace + "(" +  formatMinHDSpace(min,lFreeSpace) + ")\n");
        
        if (lMin < lFreeSpace) return true;
        if (lMin<=0) return true;
        
        if (to.isEmpty()) {
            System.out.println("Error: Missing email recipients.\n");
            return true;
        }
        
        Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(eamilAccount,password);
                }
            });

        try {

            InetAddress ip = InetAddress.getLocalHost();
            //String hostname = ip.getHostName();
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            message.setSubject(errorTitle + " in "  + ip );
            
            message.setText(drive + " free space is " + formatMinHDSpace(min,lFreeSpace) +
                    "\n\nRequired minimum free space is " + min );

            Transport.send(message);

            System.out.println("Email sent to " + to);
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }        
        return true;
    }
}

