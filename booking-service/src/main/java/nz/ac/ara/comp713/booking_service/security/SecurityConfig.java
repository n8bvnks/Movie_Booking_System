  package nz.ac.ara.comp713.booking_service.security;

  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
  import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

  @Configuration
  public class SecurityConfig implements WebMvcConfigurer {

      private final TokenStore tokenStore;

      public SecurityConfig(TokenStore tokenStore) { this.tokenStore = tokenStore; }

      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(new AuthInterceptor(tokenStore))
                  .addPathPatterns("/api/v1/**")
                  .excludePathPatterns("/api/v1/login");
      }
  }