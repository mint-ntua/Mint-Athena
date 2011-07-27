/*
This file is part of mint-athena. mint-athena services compose a web based platform that facilitates aggregation of cultural heritage metadata.
   Copyright (C) <2009-2011> Anna Christaki, Arne Stabenau, Costas Pardalis, Fotis Xenikoudakis, Nikos Simou, Nasos Drosopoulos, Vasilis Tzouvaras

   mint-athena program is free software: you can redistribute it and/or
modify
   it under the terms of the GNU Affero General Public License as
   published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package gr.ntua.ivml.athena.harvesting.concurrent;

import gr.ntua.ivml.athena.harvesting.Harvester;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledHarvestingExecutor {
	
	 private static final ScheduledExecutorService scheduler; 
	       
	 static{
		 scheduler = Executors.newScheduledThreadPool(100);
	 }
	 
	 public static void addTask(Harvester harvester, long startTime, long period){
		 scheduler.scheduleWithFixedDelay(harvester, startTime, period,  TimeUnit.MILLISECONDS);
	 }
	 
	 public static void addTask(Harvester harvester, Date startTime, long period){
		 scheduler.scheduleWithFixedDelay(harvester, startTime.getTime() - System.currentTimeMillis(), period, TimeUnit.MILLISECONDS); 
	 }
}
