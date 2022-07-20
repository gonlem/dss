<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dss="http://dss.esig.europa.eu/validation/simple-certificate-report">
                
	<xsl:output method="html" encoding="utf-8" indent="yes" omit-xml-declaration="yes" />
	
	<xsl:param name="rootTrustmarkUrlInTlBrowser">
		https://esignature.ec.europa.eu/efda/tl-browser/#/screen/tl/trustmark/
	</xsl:param>
	<xsl:param name="rootCountryUrlInTlBrowser">
		https://esignature.ec.europa.eu/efda/tl-browser/#/screen/tl/
	</xsl:param>
	
   	<xsl:variable name="validationTime">
   		<xsl:value-of select="/dss:SimpleCertificateReport/@ValidationTime" />
   	</xsl:variable>

    <xsl:template match="/dss:SimpleCertificateReport">
		<xsl:comment>Generated by DSS v.${project.version}</xsl:comment>
	    
		<xsl:apply-templates select="dss:Chain"/>
    </xsl:template>
    
	<xsl:template match="dss:Chain">
    	<xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="dss:ChainItem">
    
		<xsl:variable name="idCert" select="dss:id" />
     	<xsl:variable name="indicationText" select="dss:Indication/text()"/>
        <xsl:variable name="indicationCssClass">
        	<xsl:choose>
				<xsl:when test="$indicationText='PASSED' or dss:trustAnchors">success</xsl:when>
				<xsl:when test="$indicationText='INDETERMINATE'">warning</xsl:when>
				<xsl:when test="$indicationText='FAILED'">danger</xsl:when>
				<!-- Cannot conclude (untrusted chain) -->
				<xsl:otherwise>secondary</xsl:otherwise>
			</xsl:choose>
        </xsl:variable>
    
		<div>
    		<xsl:attribute name="class">card mb-3</xsl:attribute>
    		<div>
    			<xsl:attribute name="class">card-header bg-<xsl:value-of select="$indicationCssClass" /> text-white</xsl:attribute>
	    		<xsl:attribute name="data-target">#collapseCert-<xsl:value-of select="$idCert"/></xsl:attribute>
		       	<xsl:attribute name="data-toggle">collapse</xsl:attribute>

				<span>
					Certificate
					<xsl:value-of select="$idCert" />
				</span>
				<i>
					<xsl:attribute name="class">id-copy fa fa-clipboard btn btn-outline-light cursor-pointer text-light border-0 p-2 ml-1 mr-1</xsl:attribute>
					<xsl:attribute name="data-id"><xsl:value-of select="$idCert"/></xsl:attribute>
					<xsl:attribute name="data-toggle">tooltip</xsl:attribute>
					<xsl:attribute name="data-placement">right</xsl:attribute>
					<xsl:attribute name="data-success-text">Id copied successfully!</xsl:attribute>
					<xsl:attribute name="title">Copy Id to clipboard</xsl:attribute>
				</i>
	        </div>
    		<div>
    			<xsl:attribute name="class">card-body collapse show</xsl:attribute>
	        	<xsl:attribute name="id">collapseCert-<xsl:value-of select="$idCert"/></xsl:attribute>
	        	
	        	<xsl:if test="dss:qualificationAtIssuance or dss:qualificationAtValidation">
		        	<dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			    		
			    		<dt>
			        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		Qualification
			        	</dt>
			    		
			    		<dd>
			        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
			    		
							<ul>
	            				<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
			    		
			    				<li>
			    					Issuance Time (<xsl:call-template name="formatdate"><xsl:with-param name="DateTimeStr" select="dss:notBefore"/></xsl:call-template>) :
					    			<span>
					    				<xsl:attribute name="class">
					    					<xsl:choose>
					    						<xsl:when test="dss:qualificationAtIssuance='N/A'">badge badge-secondary</xsl:when>
					    						<xsl:otherwise>badge badge-primary</xsl:otherwise>
					    					</xsl:choose>
					    				</xsl:attribute>
					    				
					    				<xsl:value-of select="dss:qualificationAtIssuance"/>
					    			</span>
					    		</li>
					    		
			    				<li>
				    				Validation Time (<xsl:call-template name="formatdate"><xsl:with-param name="DateTimeStr" select="$validationTime"/></xsl:call-template>) :
					    			<span>
					    				<xsl:attribute name="class">
					    					<xsl:choose>
					    						<xsl:when test="dss:qualificationAtValidation='N/A'">badge badge-secondary</xsl:when>
					    						<xsl:otherwise>badge badge-primary</xsl:otherwise>
					    					</xsl:choose>
					    				</xsl:attribute>
					    				
					    				<xsl:value-of select="dss:qualificationAtValidation"/>
					    			</span>
					    		</li>
					    	</ul>
			    		</dd>
		        	</dl>
	        	</xsl:if>
	        	
	        	<xsl:if test="dss:enactedMRA">
					<dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			            	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            </dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
							The qualification level has been determined using an enacted trust service equivalence mapping.
			            </dd>
					</dl>
				</xsl:if>			
	        	
				<xsl:apply-templates select="dss:subject"/>
				
				<xsl:if test="dss:keyUsages or dss:extendedKeyUsages">
					<div>
						<xsl:attribute name="class">row mb-0</xsl:attribute>
						
						<div>
							<xsl:attribute name="class">col-md-6</xsl:attribute>
						
							<xsl:apply-templates select="dss:keyUsages"/>
						</div>
						<div>
							<xsl:attribute name="class">col-md-6</xsl:attribute>
						
							<xsl:apply-templates select="dss:extendedKeyUsages"/>
						</div>
					</div>
				</xsl:if>
					
	        	<dl>
	        		<xsl:attribute name="class">row mb-0</xsl:attribute>
	        		
	        		<dt>
		        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
		        		Validity
			        </dt>
	        		<dd>
	        			<xsl:attribute name="class">col-sm-9</xsl:attribute>
						<xsl:call-template name="formatdate">
							<xsl:with-param name="DateTimeStr" select="dss:notBefore"/>
						</xsl:call-template>
						-
						<xsl:call-template name="formatdate">
							<xsl:with-param name="DateTimeStr" select="dss:notAfter"/>
						</xsl:call-template>
	        		</dd>
	        		
	        		<xsl:if test="not(dss:trustAnchors)">
	       				<dt>
			        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		
			        		Revocation
			        	</dt>
	       				<dd>
	        				<xsl:attribute name="class">col-sm-9</xsl:attribute>
		       				<xsl:apply-templates select="dss:revocation"/>
		       			</dd>
       				</xsl:if>
	        		
					<xsl:apply-templates select="dss:ocspUrls"/>
					<xsl:apply-templates select="dss:crlUrls"/>
				</dl>
				
	        	<xsl:if test="dss:aiaUrls or dss:cpsUrls">
					<dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			    		
						<xsl:apply-templates select="dss:aiaUrls"/>
						<xsl:apply-templates select="dss:cpsUrls"/>
		        	</dl>
	        	</xsl:if>
				
	        	<xsl:if test="dss:trustAnchors">
					<dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			    		
						<xsl:apply-templates select="dss:trustAnchors"/>
		        	</dl>
	        	</xsl:if>
    		</div>
    	</div>
    </xsl:template>
    
    <xsl:template match="dss:subject">
     	<dl>
		    <xsl:attribute name="class">row mb-0</xsl:attribute>
	  		<xsl:if test="dss:commonName">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Common name
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:commonName"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:givenName">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Given name
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:givenName"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:surname">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Surname
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:surname"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:pseudonym">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
		        	Pseudonym
		        </dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:pseudonym"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:organizationName">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Organization name
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:organizationName"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:organizationUnit">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Organization Unit
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:organizationUnit"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:email">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Email
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:email"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:locality">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Locality
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:locality"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:state">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		State
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:state"/>
	        	</dd>
	  		</xsl:if>
	  		<xsl:if test="dss:country">
		   		<dt>
	        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
	        		Country
	        	</dt>
		   		<dd>
	        		<xsl:attribute name="class">col-sm-9</xsl:attribute>
	        		<xsl:value-of select="dss:country"/>
	        	</dd>
	  		</xsl:if>
	  	</dl>
	</xsl:template>
	
	<xsl:template match="dss:keyUsages">
     	<dl>
		    <xsl:attribute name="class">row mb-0</xsl:attribute>
		    
		    <dt>
	        	<xsl:attribute name="class">col-sm-6</xsl:attribute>
	        	Key usages
	        </dt>
			<dd>
	       		<xsl:attribute name="class">col-sm-6</xsl:attribute>
	   		
				<ul>
	        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
				
					<xsl:apply-templates select="dss:keyUsage"/>
				</ul>
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template match="dss:extendedKeyUsages">
     	<dl>
		    <xsl:attribute name="class">row mb-0</xsl:attribute>
		    
		    <dt>
	        	<xsl:attribute name="class">col-sm-6</xsl:attribute>
	        	Extended key usages
	        </dt>
			<dd>
	       		<xsl:attribute name="class">col-sm-6</xsl:attribute>
	   		
				<ul>
	        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
				
					<xsl:apply-templates select="dss:extendedKeyUsage"/>
				</ul>
			</dd>
		</dl>
	</xsl:template>
	
    <xsl:template match="dss:revocation">
    	<xsl:choose>
			<xsl:when test="dss:revocationDate">
				<i>
					<xsl:attribute name="class">fa fa-times-circle text-danger</xsl:attribute>
					<xsl:attribute name="title">Revoked</xsl:attribute>
				</i>
     			Revoked (reason:<xsl:value-of select="dss:revocationReason" /> @
				<xsl:call-template name="formatdate">
					<xsl:with-param name="DateTimeStr" select="dss:revocationDate"/>
				</xsl:call-template>)
			</xsl:when>    	
			<xsl:when test="dss:thisUpdate">
      			<i>
					<xsl:attribute name="class">fa fa-check-circle text-success</xsl:attribute>
					<xsl:attribute name="title">Not Revoked</xsl:attribute>
				</i>
			</xsl:when>
			<xsl:otherwise>
      			<i>
					<xsl:attribute name="class">fa fa-question-circle text-muted</xsl:attribute>
					<xsl:attribute name="title">No revocation data available</xsl:attribute>
				</i>
			</xsl:otherwise>
    	</xsl:choose>
    </xsl:template>
    
    <xsl:template match="dss:ocspUrls">
  		<dt>
			<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		
  			<acronym>
  				<xsl:attribute name="title">Online Certificate Status Protocol</xsl:attribute>
  				OCSP
  			</acronym>
		</dt>
		<dd>
       		<xsl:attribute name="class">col-sm-9</xsl:attribute>
   		
			<ul>
        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
   				
   				<xsl:apply-templates select="dss:ocspUrl"/>
   			</ul>
   		</dd>
	</xsl:template>
	
  	<xsl:template match="dss:crlUrls">
  		<dt>
			<xsl:attribute name="class">col-sm-3</xsl:attribute>
			
  			<acronym>
  				<xsl:attribute name="title">Certificate Revocation List</xsl:attribute>
		  		CRL
		  	</acronym>
		</dt>
		<dd>
       		<xsl:attribute name="class">col-sm-9</xsl:attribute>
   		
			<ul>
        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
   				
				<xsl:apply-templates select="dss:crlUrl"/>
			</ul>
		</dd>
	</xsl:template>
	
	<xsl:template match="dss:aiaUrls">
  		<dt>
			<xsl:attribute name="class">col-sm-3</xsl:attribute>
			
  			<acronym>
  				<xsl:attribute name="title">Authority Information Access</xsl:attribute>
  		  		AIA
  		  	</acronym>
		</dt>
		<dd>
       		<xsl:attribute name="class">col-sm-9</xsl:attribute>
   		
			<ul>
        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
   				
				<xsl:apply-templates select="dss:aiaUrl"/>
			</ul>
		</dd>
	</xsl:template>
	
    <xsl:template match="dss:cpsUrls">
  		<dt>
			<xsl:attribute name="class">col-sm-3</xsl:attribute>
			
  			<acronym>
  				<xsl:attribute name="title">Certification Practice Statements</xsl:attribute>
  		  		CPS
  		  	</acronym>
		</dt>
		
		<dd>
       		<xsl:attribute name="class">col-sm-9</xsl:attribute>
   		
			<ul>
        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
   				
				<xsl:apply-templates select="dss:cpsUrl"/>
			</ul>
		</dd>
	</xsl:template>
	
    <xsl:template match="dss:trustAnchors">
  		<dt>
			<xsl:attribute name="class">col-sm-3</xsl:attribute>
			
  			Trust Anchor
		</dt>
		
		<dd>
       		<xsl:attribute name="class">col-sm-9</xsl:attribute>
   		
			<ul>
        		<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
   				
				<xsl:apply-templates select="dss:trustAnchor"/>
			</ul>
		</dd>
	</xsl:template>
    
    <xsl:template match="dss:trustAnchor">
    	<li>
    		<a>
    			<xsl:attribute name="href">
	    			<xsl:value-of select="concat($rootCountryUrlInTlBrowser, dss:countryCode)" />
	    		</xsl:attribute>
	    		<xsl:attribute name="target">_blank</xsl:attribute>
	    		<xsl:attribute name="title"><xsl:value-of select="dss:countryCode" /></xsl:attribute>
	    		
	    		<span>
	    			<xsl:attribute name="class">
		    			small_flag <xsl:value-of select="concat('flag_', dss:countryCode)" />
		    		</xsl:attribute>
	    		</span>
    		</a>
    		
    		<i>
    			<xsl:attribute name="class">fa fa-arrow-circle-right ml-2 mr-2</xsl:attribute>
    		</i>
    		
    		<a>
	    		<xsl:attribute name="href">
	    			<xsl:value-of select="concat($rootTrustmarkUrlInTlBrowser, dss:countryCode, '/', dss:trustServiceProviderRegistrationId)" />
	    		</xsl:attribute>
	    		<xsl:attribute name="target">_blank</xsl:attribute>
	    		<xsl:attribute name="title">View in TL Browser</xsl:attribute>
	    		
	    		<xsl:value-of select="dss:trustServiceProvider" /> 
    		</a>
    		
    		<i>
    			<xsl:attribute name="class">fa fa-arrow-circle-right ml-2 mr-2</xsl:attribute>
    		</i>
    		
    		<xsl:value-of select="dss:trustServiceName" />
    	</li>
    </xsl:template>
    
    <xsl:template match="dss:keyUsage | dss:extendedKeyUsage">
    	<li><xsl:value-of select="." /></li>
    </xsl:template>
    
    <xsl:template match="dss:ocspUrl | dss:crlUrl | dss:aiaUrl | dss:cpsUrl | dss:pdsUrl">
    	<li>
    		<a>
    			<xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute>
	    		<xsl:attribute name="target">_blank</xsl:attribute>
	    		
    			<xsl:value-of select="." />
    		</a>
    	</li>
    </xsl:template>

	<xsl:template name="formatdate">
		<xsl:param name="DateTimeStr" />

		<xsl:variable name="date">
			<xsl:value-of select="substring-before($DateTimeStr,'T')" />
		</xsl:variable>

		<xsl:variable name="after-T">
			<xsl:value-of select="substring-after($DateTimeStr,'T')" />
		</xsl:variable>

		<xsl:variable name="time">
			<xsl:value-of select="substring-before($after-T,'Z')" />
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="string-length($date) &gt; 0 and string-length($time) &gt; 0">
				<xsl:value-of select="concat($date,' ', $time)" />
			</xsl:when>
			<xsl:when test="string-length($date) &gt; 0">
				<xsl:value-of select="$date" />
			</xsl:when>
			<xsl:when test="string-length($time) &gt; 0">
				<xsl:value-of select="$time" />
			</xsl:when>
			<xsl:otherwise>-</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
