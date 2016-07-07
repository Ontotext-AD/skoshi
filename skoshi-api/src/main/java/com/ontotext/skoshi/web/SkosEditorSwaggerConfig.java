package com.ontotext.skoshi.web;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Configuration
@EnableSwagger //Loads the spring beans required by the framework
public class SkosEditorSwaggerConfig implements ServletContextAware {

	@Autowired
	private SpringSwaggerConfig springSwaggerConfig;

	private ServletContext servletContext;

	@Override public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}


	protected SwaggerPathProvider getPathProvider() {
		SwaggerPathProvider provider = new RelativeSwaggerPathProvider();
		String contextPath = servletContext.getContextPath();
		if (contextPath.startsWith("/")) {
			contextPath = contextPath.substring(1);
		}
		provider.setApiResourcePrefix(contextPath);
		return provider;
	}

	/**
	 * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
	 * swagger groups i.e. same code base multiple swagger resource listings.
	 */
	@Bean
	public SwaggerSpringMvcPlugin customImplementation(){
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
				.pathProvider(getPathProvider());
	}

}
