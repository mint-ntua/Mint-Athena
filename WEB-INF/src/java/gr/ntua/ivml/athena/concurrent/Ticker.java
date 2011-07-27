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

package gr.ntua.ivml.athena.concurrent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple class to enable regular reports.
 * Use like this:
 * t = new Ticker( 60 )  //every 60 seconds the Ticker becomes set.
 * if( t.isSet() ) { t.reset(), report } t.cancel()
 * 
 * 
 * @author Arne Stabenau 
 *
 */
public class Ticker extends TimerTask {
	private static final Timer t = new Timer();
	private boolean flag;
	private int seconds;
	private boolean restartOnReset;
	
	public Ticker( int seconds ) {
		flag = false;
		restartOnReset = false;
		this.seconds = seconds;
		t.schedule( this, seconds*1000l, seconds*1000l);
	}
	
	/**
	 * If restart on Reset is true, the ticker only starts counting from
	 * the last reset. 
	 * @param seconds
	 * @param restartOnReset
	 */
	public Ticker( int seconds, boolean restartOnReset ) {
		this.restartOnReset = restartOnReset;
		this.seconds = seconds;
		this.flag = true;
	}
	
	public void run() {
		flag = true;
	}

	public void reset() {
		flag = false;
		if( restartOnReset ) {
			t.schedule(this, seconds*1000l);
		}
	}
	public boolean isSet() {
		return flag;
	}
}
