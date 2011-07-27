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

package gr.ntua.ivml.athena.util;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class EncodingFilter implements Filter {

	private String encoding = "UTF-8";

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
		throws IOException, ServletException {

		request.setCharacterEncoding(encoding); 
		response.setCharacterEncoding(encoding); 
		filterChain.doFilter(request, response); 
	}

	public void init(FilterConfig filterConfig) throws ServletException { 
		// TODO Auto-generated method stub String encodingParam = filterConfig.getInitParameter("encoding"); if (encodingParam != null) { encoding = encodingParam; }

	} 
} 