/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.analysis;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class AnalysisRunExampleLocal {
	private static final Logger log = Logger.getLogger(AnalysisRunExampleLocal.class);
			
	public static void main(String[] args) throws IOException {
		String runDirectoryA ="./scenarioA/";
		String runDirectoryB = "./scenarioB/";
		String runIdA = "true";
		String runIdB = "true";
		String outputPath = "";
		boolean scenarioBpresent = FALSE;
		
		if(args.length>1){
			// add trailing slash if not present
			runDirectoryA = args[0].substring(args[0].length()-1).equals("/")	?	args[0]	:	args[0]+"/";
			runIdA = args.length>1 ? args[1] : runIdA;
		}
		if(args.length>3){
			runDirectoryB = args[2].substring(args[2].length()-1).equals("/")	?	args[2]	:	args[2]+"/";
			runIdB = args.length>3 ? args[3] : runIdB;
			scenarioBpresent = TRUE;
		}
		if(args.length>4){
			outputPath = args[4].substring(args[4].length()-1).equals("/")	?	args[4]	:	args[4]+"/";
		}

		final String modesString = "car,pt,bike,walk,ride";
		String scenarioCRS = "EPSG:1234";
		scenarioCRS=null;
// scenario A
		Scenario scenarioA = loadScenario(runDirectoryA, runIdA, scenarioCRS);
		List<AgentFilter> scenarioAFilters = new ArrayList<>();
		AgentAnalysisFilter filter1a = new AgentAnalysisFilter("A");

		//filter1a.setZoneFile("./berlin_borders/berlin_epsg_4326.shp");

		filter1a.preProcess(scenarioA);
		scenarioAFilters.add(filter1a);

		List<TripFilter> tripFilterList = new ArrayList<>();
		TripAnalysisFilter tripAnalysisFilter = new TripAnalysisFilter("moin");
		tripAnalysisFilter.preProcess(scenarioA);
		tripFilterList.add(tripAnalysisFilter);
		
		List<String> modes = new ArrayList<>();
		MatsimAnalysis analysis = new MatsimAnalysis();

// scenario B
		if(scenarioBpresent){
			analysis.setScenario0(scenarioA);

			Scenario scenarioB = loadScenario(runDirectoryB, runIdB, scenarioCRS);
			AgentAnalysisFilter filter2a = new AgentAnalysisFilter("B");
			//filter2a.setZoneFile("./berlin_borders/berlin_epsg_4326.shp");
			filter2a.preProcess(scenarioB);
			scenarioAFilters.add(filter2a);

			TripAnalysisFilter tripAnalysisFilter2 = new TripAnalysisFilter("moin");
			tripAnalysisFilter2.preProcess(scenarioB);
			tripFilterList.add(tripAnalysisFilter2);
			analysis.setScenario1(scenarioB);
		} else {
			analysis.setScenario1(scenarioA);
		}
		
		analysis.setAgentFilters(scenarioAFilters);
		analysis.setTripFilters(tripFilterList);
		analysis.setModes(modes);
		analysis.setScenarioCRS(scenarioCRS);
		
		if(!outputPath.isEmpty()){
			analysis.setAnalysisOutputDirectory(outputPath);
		}
		
		analysis.run();
	}
	
	private static Scenario loadScenario(String runDirectory, String runId, String scenarioCRS) {
		log.info("Loading scenario...");
		
		if (runDirectory == null || runDirectory.equals("") || runDirectory.equals("null")) {
			return null;	
		}
		
		if (!runDirectory.endsWith("/")) runDirectory = runDirectory + "/";

		String networkFile;
		String populationFile;
		String facilitiesFile;
		
		networkFile = runDirectory + runId + ".output_network.xml.gz";
		populationFile = runDirectory + runId + ".output_plans.xml.gz";
		facilitiesFile = runDirectory + runId + ".output_facilities.xml.gz";

		Config config = ConfigUtils.createConfig();

		config.global().setCoordinateSystem(scenarioCRS);
		config.controler().setRunId(runId);
		config.controler().setOutputDirectory(runDirectory);
		config.plans().setInputFile(populationFile);
		config.network().setInputFile(networkFile);
		config.facilities().setInputFile(facilitiesFile);
		
		
		return ScenarioUtils.loadScenario(config);
	}

}
		

