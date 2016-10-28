<c:set var="offsetDisp" value="${rf.resultsOffset+1}"/><c:set var="calcEndNum" value="${rf.resultsOffset+rf.resultsLength}"/>
<!-- Showing records ${offsetDisp} through ${(calcEndNum > fn:length(rf.results) ? fn:length(rf.results) : calcEndNum)} out of ${fn:length(rf.results)} total  -->
