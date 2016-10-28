	<style type="text/css">
	#progress-indicator {
		text-align:center;
		width:400px;
		border:3px black solid;
		padding:10px 5px 10px 5px;
	}
	
	#progress-bar {
		height:15px;
		width:200px;
		border:thin black solid;
		background-color:white;
	}
	
	#progress-bar-img {
/* 		position:relative;
		top:2px;
		left:0px; */
		height:100%;
		width:5px;
		border:none;
	}
	</style>

	<div id="progress-indicator" align="center" style="display:none">
		<div align="center">
		<table>
			<tr>
				<td align="left">
					<span id="progress-msg"></span>&nbsp;
					(<span id="progress-done"></span> of
					<span id="progress-total"></span>)
				
				</td>
				<td align="right"><span id="percent-complete"></span>%</td>
			</tr>
			<tr valign="middle">
				<td colspan="2">
					<div id="progress-bar" align="left">
						<img id="progress-bar-img" src="../images/progressbar.jpg" />
					</div>
				</td>
			</tr>
		</table>
		</div>
	</div>
