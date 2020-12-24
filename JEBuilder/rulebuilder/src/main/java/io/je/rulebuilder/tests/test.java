package io.je.rulebuilder.tests;

import java.util.ArrayList;

public class test {

	public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		for(int i = 0; i<list.size()-1;i++)
		{
			System.out.println(list.get(i));
		}
		
		System.out.println(list.get(list.size()-1));
		

	}

}
