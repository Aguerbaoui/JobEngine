package io.je.rulebuilder.tests;

import java.util.ArrayList;

public class test {

	public static void main(String[] args) {

		String s = "";
		 String str = "arm"; 
	        String[] a = str.split("\\.", 5); 
	  
	        for(int i=0;i<a.length-1;i++)
	        {
	        	a[i]=a[i].substring(0, 1).toUpperCase() + a[i].substring(1);
	        	a[i] = "get" + a[i] +"()";
	        	s=s+a[i]+".";
	        }
	        a[a.length-1]=a[a.length-1].substring(0, 1).toUpperCase() + a[a.length-1].substring(1);
	        a[a.length-1] = "get" + a[a.length-1] +"()";
        	s=s+a[a.length-1];
	       
	        System.out.println(s);
	            
	    } 
	

}
