package org.jfantasy.framework.security.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.oauth.userdetails.OAuthUserDetailsService;
import org.jfantasy.security.data.SecurityStorage;
import org.jfantasy.security.web.intercept.SecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.event.LoggerListener;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.ToKenAuthenticationFilter;
import org.springframework.security.web.authentication.www.TokenAuthenticationEntryPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: <类功能描述>. <br>
 * <p>
 * <使用说明>
 * </p>
 * Makedate:2014年8月18日 上午11:09:27
 *
 * @author Administrator
 * @version V1.0
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Log LOG = LogFactory.getLog(SpringSecurityConfig.class);

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/oauth/token","/oauth/access_token","/api-docs");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 设置拦截规则

//        http.authorizeRequests().antMatchers("/**").hasRole("USER").antMatchers("/**/*.doc").hasRole("ADMIN");

//        // 自定义accessDecisionManager访问控制器,并开启表达式语言
//        http.authorizeRequests().accessDecisionManager(accessDecisionManager()).expressionHandler(webSecurityExpressionHandler()).and().exceptionHandling().accessDeniedPage("/login");

//        // 开启默认登录页面
//        http.formLogin();

//        // 自定义登录页面
//        http.csrf().disable().formLogin().loginPage("/login").failureUrl("/login?error=1").loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").permitAll();

//        // 自定义注销
//        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true);

        // session管理
//        http.sessionManagement().sessionFixation().changeSessionId().maximumSessions(1).expiredUrl("/");

        // RemeberMe
//        http.rememberMe().key("rememberme");

//        http.httpBasic();

//        http.csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository());

        http.csrf().disable();

        ToKenAuthenticationFilter filter = new ToKenAuthenticationFilter(authenticationManager(), new TokenAuthenticationEntryPoint());
        http.addFilterBefore(filter, BasicAuthenticationFilter.class);

//        http.addFilter(new FilterSecurityInterceptor());
    }

    @Autowired
    public SecurityStorage securityStorage;

    @Bean
    public FilterSecurityInterceptor filterSecurityInterceptor() {
        FilterSecurityInterceptor securityInterceptor = new FilterSecurityInterceptor();
        securityInterceptor.setSecurityMetadataSource(securityMetadataSource());
        securityInterceptor.setAccessDecisionManager(accessDecisionManager());
        return securityInterceptor;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 设置内存用户角色//.authenticationProvider(authenticationProvider())
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new OAuthUserDetailsService(this.securityStorage);
    }

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new SecurityMetadataSource(this.securityStorage);
    }

    @Bean
    public LoggerListener loggerListener() {
        LOG.info("org.springframework.security.authentication.event.LoggerListener");
        return new LoggerListener();
    }

    @Bean
    public org.springframework.security.access.event.LoggerListener eventLoggerListener() {
        LOG.info("org.springframework.security.access.event.LoggerListener");
        return new org.springframework.security.access.event.LoggerListener();
    }

    /*
     *
     * 这里可以增加自定义的投票器
     */
    @SuppressWarnings("rawtypes")
    @Bean(name = "accessDecisionManager")
    public AccessDecisionManager accessDecisionManager() {
        LOG.info("AccessDecisionManager");
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new RoleVoter());
        decisionVoters.add(new AuthenticatedVoter());
        decisionVoters.add(webExpressionVoter());// 启用表达式投票器
        return new AffirmativeBased(decisionVoters);
    }

    /*
     * 表达式控制器
     */
    @Bean(name = "expressionHandler")
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        LOG.info("DefaultWebSecurityExpressionHandler");
        return new DefaultWebSecurityExpressionHandler();
    }

    /*
     * 表达式投票器
     */
    @Bean(name = "expressionVoter")
    public WebExpressionVoter webExpressionVoter() {
        LOG.info("WebExpressionVoter");
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(webSecurityExpressionHandler());
        return webExpressionVoter;
    }

}
