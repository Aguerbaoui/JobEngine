package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.RuleTemplate;
import io.je.rulebuilder.config.RuleBuilderConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.template.ObjectDataCompiler;

public class DRLBuilder {
	
	
	public void generateDRL(RuleTemplate rule)
	{
		  ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
		  Map<String, String> data = new HashMap<>();
		  data.put("name",rule.getRuleName());
		  data.put("salience",rule.getSalience());
		  data.put("duration",rule.getDuration());
		  data.put("condition",rule.getCondition());
		  data.put("actions",rule.getConsequence());
		  String ruleContent="k";
		  try {
			ruleContent= objectDataCompiler.compile(Arrays.asList(data),new FileInputStream(RuleBuilderConfig.ruleTemplatePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		  
		  try {
		      File myObj = new File(RuleBuilderConfig.drlGenerationPath+rule.getRuleName()+".drl");
		      myObj.createNewFile() ;		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		  
		  try {
		      FileWriter myWriter = new FileWriter(RuleBuilderConfig.drlGenerationPath+rule.getRuleName()+".drl");
		      myWriter.write(ruleContent);
		      myWriter.close();
		      System.out.println("Successfully generated drl");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}

}
