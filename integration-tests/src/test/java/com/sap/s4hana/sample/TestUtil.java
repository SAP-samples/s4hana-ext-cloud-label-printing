package com.sap.s4hana.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientsThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.servlet.RequestAccessorFilter;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;
import com.sap.s4hana.sample.validation.ReflectionParameterNameProvider;

import io.restassured.mapper.ObjectMapperType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class TestUtil
{
    public static WebArchive createDeployment( final Class<?>... classesUnderTest )
    {
        return ShrinkWrap
            .create(WebArchive.class)
            .addClasses(classesUnderTest)
            .addClass(RequestAccessorFilter.class)
            .addClass(TenantThreadContextListener.class)
            .addClass(PrincipalThreadContextListener.class)
            .addClass(HttpClientsThreadContextListener.class)
            .addAsManifestResource("arquillian.xml")
            
			.addAsResource(new File("../srv/src/main/resources/META-INF/validation.xml"), "meta-inf/validation.xml")
			.addClass(ReflectionParameterNameProvider.class)
            
            .addAsWebInfResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));
    }

    public static ObjectMapperType objectMapperType()
    {
        return ObjectMapperType.JACKSON_2;
    }

    public static String toJson( final Object obj )
    {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        }
        catch( final JsonProcessingException e ) {
            throw new ShouldNotHappenException(e);
        }
    }
    
    /**
	 * Loads a file from classpath
	 * 
	 * @return file contents
	 */
	public static String loadFileAsString(final String filename) throws IOException {
		return IOUtils.toString(TestUtil.class.getResourceAsStream(filename), Charset.defaultCharset());
	}
	
}
