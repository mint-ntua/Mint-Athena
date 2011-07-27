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
import gr.ntua.ivml.athena.persistent.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HarvestingExecutor {

	private static ThreadPoolExecutor executor;
	private static BlockingQueue<Runnable> pendingTasksQueue;
	
	static{
		pendingTasksQueue = new ArrayBlockingQueue<Runnable>(100);
		executor = new ThreadPoolExecutor(8, 16, 1000, TimeUnit.MILLISECONDS, pendingTasksQueue);
	}
	
	public static void executeSigleHarvester(String url){
		Harvester harvester = new Harvester(url);
		executor.execute(harvester);
	}
	
	public static void executeSigleHarvester(String url, User user){
		Harvester harvester = new Harvester(url, user);
		executor.execute(harvester);
	}
	
	public static void executeMultipleHarvesters(ArrayList<String> urls){
		Iterator<String> itr = urls.iterator();
		while(itr.hasNext()){
			Harvester harvester = new Harvester(itr.next());
			executor.execute(harvester);
		}
	}
	
	public static void shutdown(){
		executor.shutdown();
	}
}

	