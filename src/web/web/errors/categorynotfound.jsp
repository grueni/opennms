<!--

//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2003 Feb 07: Fixed URLEncoder issues.
// 
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com///

-->

<%@page language="java" contentType="text/html" session="true" isErrorPage="true" import="org.opennms.web.category.*"%>

<html>
<head>
  <title>Category Not Found | Error | OpenNMS Web Console</title>
  <base HREF="<%=org.opennms.web.Util.calculateUrlBase( request )%>" />
  <link rel="stylesheet" type="text/css" href="includes/styles.css" />
</head>
<body marginwidth="0" marginheight="0" leftmargin="0" rightmargin="0" topmargin="0">

<% String breadcrumb1 = "Error"; %>
<jsp:include page="/WEB-INF/jspf/header.jspf" flush="false" >
  <jsp:param name="title" value="Error" />
  <jsp:param name="breadcrumb" value="<%=breadcrumb1%>" />
</jsp:include>

<br />

<%
    CategoryNotFoundException cnfe = null;
    
    if( exception instanceof CategoryNotFoundException ) {
        cnfe = (CategoryNotFoundException)exception;
    }
    else if( exception instanceof ServletException ) {
        cnfe = (CategoryNotFoundException)((ServletException)exception).getRootCause();
    }
    else {
        throw new ServletException( "This error page does not handle this exception type.", exception );
    }    
    
%>

<!-- Body -->

<table width="100%" border="0" cellspacing="0" cellpadding="2">
  <tr>
    <td> &nbsp; </td>

    <td>
      <h1>Category Not Found</h1>

      <p>
        No information is currently available for the <strong><%=cnfe.getCategory()%></strong> category.
        <br>
        Either the category does not exist, or its information is still being calculated.  
      </p>
    </td>

    <td> &nbsp; </td>
  </tr>
</table>                               

<br />


<jsp:include page="/WEB-INF/jspf/footer.jspf" flush="false" />

</body>
</html>
