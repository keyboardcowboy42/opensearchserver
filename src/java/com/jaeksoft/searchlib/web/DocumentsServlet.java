/**   
 * License Agreement for Jaeksoft OpenSearchServer
 *
 * Copyright (C) 2008-2011 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of Jaeksoft OpenSearchServer.
 *
 * Jaeksoft OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Jaeksoft OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jaeksoft OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.jaeksoft.searchlib.Client;
import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.remote.StreamReadObject;
import com.jaeksoft.searchlib.render.Render;
import com.jaeksoft.searchlib.render.RenderObject;
import com.jaeksoft.searchlib.request.DocumentsRequest;
import com.jaeksoft.searchlib.result.ResultDocuments;
import com.jaeksoft.searchlib.user.Role;
import com.jaeksoft.searchlib.user.User;

public class DocumentsServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -343590007504141181L;

	/**
	 * 
	 */

	private Render doObjectRequest(Client client,
			ServletTransaction servletTransaction) throws ServletException {
		StreamReadObject sro = null;
		try {
			sro = new StreamReadObject(servletTransaction.getInputStream());
			DocumentsRequest documentsRequest = (DocumentsRequest) sro.read();
			ResultDocuments resultDocuments = client
					.documents(documentsRequest);
			return new RenderObject(resultDocuments);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			if (sro != null)
				sro.close();
		}
	}

	@Override
	protected void doRequest(ServletTransaction servletTransaction)
			throws ServletException {

		try {

			User user = servletTransaction.getLoggedUser();
			if (user != null
					&& !user.hasRole(servletTransaction.getIndexName(),
							Role.INDEX_QUERY))
				throw new SearchLibException("Not permitted");

			Client client = servletTransaction.getClient();

			Render render = doObjectRequest(client, servletTransaction);

			render.render(servletTransaction);

		} catch (Exception e) {
			throw new ServletException(e);
		}

	}

	public static ResultDocuments documents(URI uri,
			DocumentsRequest documentsRequest) throws IOException,
			URISyntaxException, ClassNotFoundException {
		return (ResultDocuments) sendReceiveObject(
				buildUri(uri, "/documents", null, null), documentsRequest);
	}
}
