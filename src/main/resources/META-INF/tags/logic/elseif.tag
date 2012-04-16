<%@tag import="java.util.List"%>
<%@ attribute name="condition" required="true" %>
<%@ attribute name="suppressVersion" required="false" type="java.lang.Boolean" description="If set to true, scripts will not append the application version as a request parameter. This is useful to keep resources inline with application deployments" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- 
Some nasty scriplet love lets us have nicer if/else if/else blocks. Just close your eyes and act like nothing is wrong...

We maintain a stack of booleans shared across the if/else/elseif tags. 
This allows nested if statements to function, and gives access to the evaluation conditions
to the other tags. 
--%>
<c:set var="evaluated" value="${false}"/>
<c:if test="${condition}"><c:set var="evaluated" value="${true}"/></c:if>
<% 
	List<Boolean> logicStack = (List<Boolean>)getJspContext().getAttribute("logicTagStack", PageContext.REQUEST_SCOPE);
	if(logicStack == null){
		throw new IllegalStateException("Unable to execute elseif tag without a preceding if tag");	
	}
	int currentSize = logicStack.size();
	boolean currentEvaluated  = logicStack.get(logicStack.size() - 1);
	boolean evaluated = Boolean.TRUE.equals(getJspContext().getAttribute("evaluated"));
	if(!currentEvaluated && evaluated){
		logicStack.set(logicStack.size() - 1, true);
%>
<jsp:doBody/>
<% 
	}
	while(logicStack.size() > currentSize){
		logicStack.remove(currentSize);
	}
%>