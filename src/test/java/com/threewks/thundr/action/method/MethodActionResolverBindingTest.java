package com.threewks.thundr.action.method;

import static com.atomicleopard.expressive.Expressive.*;
import static com.threewks.thundr.matchers.ExtendedMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jodd.util.ReflectUtil;

import org.junit.Before;
import org.junit.Test;

import com.threewks.thundr.action.method.bind.http.HttpBinder;
import com.threewks.thundr.bind.DeepJavaBean;
import com.threewks.thundr.bind.JavaBean;
import com.threewks.thundr.bind.TestBindTo;

public class MethodActionResolverBindingTest {

	private static Object nullObject = null;
	private static Class<Object> o = Object.class;
	private Map<String, String> emptyMap = Collections.emptyMap();
	private HttpBinder binder = new HttpBinder();

	private HttpServletRequest request;
	private MethodActionResolver resolver = new MethodActionResolver(null);

	@Before
	public void before() {
		request = mock(HttpServletRequest.class);
		when(request.getParameterMap()).thenReturn(Collections.<String, String[]> emptyMap());
	}

	@Test
	public void shouldInvokeNone() throws Exception {
		MethodAction method = method("methodNone");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(0));
		assertThat(resolver.bindArguments(method, request("argument1", "value1"), null, null).size(), is(0));
	}

	private MethodAction method(String method) throws ClassNotFoundException {
		MethodAction actionMethod = new MethodAction(TestBindTo.class, ReflectUtil.findMethod(TestBindTo.class, method), noInterceptors());
		return actionMethod;
	}

	@Test
	public void shouldInvokeSingleString() throws Exception {
		MethodAction method = method("methodSingleString");
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1", "value1"), null, emptyMap), isList(o, "value1"));
	}

	@Test
	public void shouldInvokeDoubleString() throws Exception {
		MethodAction method = method("methodDoubleString");
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1", "value1", "argument2", "value2"), null, emptyMap), isList(o, "value1", "value2"));
		assertThat(resolver.bindArguments(method, request("argument1", "value1"), null, emptyMap), isList(o, "value1", null));
		assertThat(resolver.bindArguments(method, request("argument2", "value2"), null, emptyMap), isList(o, null, "value2"));
	}

	@Test
	public void shouldInvokeStringList() throws Exception {
		MethodAction method = method("methodStringList");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(1));
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1[0]", "value1"), null, emptyMap), isList(o, list("value1")));
		assertThat(resolver.bindArguments(method, request("argument1[0]", "value1", "argument1[1]", "value2", "argument1[3]", "value3"), null, emptyMap),
				isList(o, list("value1", "value2", null, "value3")));
		assertThat(resolver.bindArguments(method, request("argument1[0]", "", "argument1[1]", "value2", "argument1[3]", "value3"), null, emptyMap), isList(o, list("", "value2", null, "value3")));
		assertThat(resolver.bindArguments(method, request("argument1", new String[] { "value1", "value2" }), null, emptyMap), isList(o, list("value1", "value2")));
		assertThat(resolver.bindArguments(method, request("argument1", null), null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1", ""), null, emptyMap), isList(o, nullObject));
	}

	@Test
	public void shouldInvokeStringMap() throws Exception {
		MethodAction method = method("methodMap");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(1));
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1[0]", "value1"), null, emptyMap), isList(o, map("0", list("value1"))));
		assertThat(resolver.bindArguments(method, request("argument1[first]", "value1", "argument1[second]", "value2", "argument1[third]", "value3"), null, emptyMap),
				isList(o, map("first", list("value1"), "second", list("value2"), "third", list("value3"))));
		assertThat(resolver.bindArguments(method, request("argument1[first]", "", "argument1[second_second]", "value2", "argument1[THIRD]", "value3", "argument1[fourth]", null), null, emptyMap),
				isList(o, map("first", null, "second_second", list("value2"), "THIRD", list("value3"), "fourth", null)));
		// TODO - Implicit map - what would an unindexed map posted look like?
		// assertThat(resolver.bindArguments(method("methodMap"), request("argument1", new String[] { "value1", "value2" }), null, emptyMap), isList(o, map("value1", "value2")));
	}

	@Test
	public void shouldInvokeArray() throws ClassNotFoundException {
		MethodAction method = method("methodStringArray");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(1));
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat((String[]) resolver.bindArguments(method, request("argument1[0]", "value1"), null, emptyMap).get(0), isArray(String.class, "value1"));
		assertThat((String[]) resolver.bindArguments(method, request("argument1[0]", "value1", "argument1[1]", "value2", "argument1[3]", "value3"), null, emptyMap).get(0),
				isArray(String.class, "value1", "value2", null, "value3"));
		assertThat((String[]) resolver.bindArguments(method, request("argument1[0]", "", "argument1[1]", "value2", "argument1[3]", "value3"), null, emptyMap).get(0),
				isArray(String.class, "", "value2", null, "value3"));
		assertThat((String[]) resolver.bindArguments(method, request("argument1", new String[] { "value1", "value2" }), null, emptyMap).get(0), isArray(String.class, "value1", "value2"));
		assertThat(resolver.bindArguments(method, request("argument1", null), null, emptyMap), isList(o, nullObject));
		List<Object> bind = resolver.bindArguments(method, request("argument1", ""), null, emptyMap);
		assertThat(bind.size(), is(1));
		assertThat((String[]) bind.get(0), is(new String[0]));
	}

	@Test
	public void shouldInvokeGenericArray() throws ClassNotFoundException {
		MethodAction method = method("methodGenericArray");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(1));
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat((String[]) resolver.bindArguments(method, request("argument1[0]", "value1"), null, emptyMap).get(0), isArray(String.class, "value1"));
		assertThat((String[]) resolver.bindArguments(method, request("argument1[0]", "value1", "argument1[1]", "value2", "argument1[3]", "value3"), null, emptyMap).get(0),
				isArray(String.class, "value1", "value2", null, "value3"));
		assertThat((String[]) resolver.bindArguments(method, request("argument1[0]", "", "argument1[1]", "value2", "argument1[3]", "value3"), null, emptyMap).get(0),
				isArray(String.class, "", "value2", null, "value3"));
		assertThat((String[]) resolver.bindArguments(method, request("argument1", new String[] { "value1", "value2" }), null, emptyMap).get(0), isArray(String.class, "value1", "value2"));
		assertThat(resolver.bindArguments(method, request("argument1", null), null, emptyMap), isList(o, nullObject));
		List<Object> bind = resolver.bindArguments(method, request("argument1", ""), null, emptyMap);
		assertThat(bind.size(), is(1));
		assertThat((String[]) bind.get(0), is(new String[0]));
	}

	@Test
	public void shouldInvokeJavaBean() throws ClassNotFoundException {
		MethodAction method = method("methodJavaBean");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(1));
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1.name", "myname"), null, emptyMap), isList(Object.class, new JavaBean("myname", null)));
		assertThat(resolver.bindArguments(method, request("argument1.name", "myname", "argument1.value", "my value"), null, emptyMap), isList(Object.class, new JavaBean("myname", "my value")));
		assertThat(resolver.bindArguments(method, request("argument1.value", "my value"), null, emptyMap), isList(Object.class, new JavaBean(null, "my value")));
		assertThat(resolver.bindArguments(method, request("argument1.name", new String[] { "value1", "value2" }), null, emptyMap), isList(Object.class, new JavaBean("value1,value2", null)));
		assertThat(resolver.bindArguments(method, request("argument1.name", null), null, emptyMap), isList(Object.class, new JavaBean(null, null)));
		assertThat(resolver.bindArguments(method, request("argument1.name", ""), null, emptyMap), isList(Object.class, new JavaBean("", null)));
		assertThat(resolver.bindArguments(method, request("argument1.name", "multiline\ncontent"), null, emptyMap), isList(Object.class, new JavaBean("multiline\ncontent", null)));
		assertThat(resolver.bindArguments(method, request("argument1", null), null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1", ""), null, emptyMap), isList(o, nullObject));
	}

	@Test
	public void shouldInvokeDeepJavaBean() throws ClassNotFoundException {
		MethodAction method = method("methodDeepJavaBean");
		assertThat(resolver.bindArguments(method, request, null, emptyMap).size(), is(1));
		assertThat(resolver.bindArguments(method, request, null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1.name", "myname"), null, emptyMap), isList(Object.class, new DeepJavaBean("myname", null)));
		assertThat(resolver.bindArguments(method, request("argument1.name", "myname", "argument1.beans[0].name", "some name"), null, emptyMap),
				isList(Object.class, new DeepJavaBean("myname", list(new JavaBean("some name", null)))));
		assertThat(resolver.bindArguments(method, request("argument1.name", "myname", "argument1.beans[0].name", "some name", "argument1.beans[1].name", "some other"), null, emptyMap),
				isList(Object.class, new DeepJavaBean("myname", list(new JavaBean("some name", null), new JavaBean("some other", null)))));
		assertThat(resolver.bindArguments(method, request("argument1.name", "myname", "argument1.beans[1].name", "some name"), null, emptyMap),
				isList(Object.class, new DeepJavaBean("myname", list(null, new JavaBean("some name", null)))));
		assertThat(resolver.bindArguments(method, request("argument1", null), null, emptyMap), isList(o, nullObject));
		assertThat(resolver.bindArguments(method, request("argument1", ""), null, emptyMap), isList(o, nullObject));
	}

	private HttpServletRequest request(String... args) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (int i = 0; i < args.length; i += 2) {
			map.put(args[i], new String[] { args[i + 1] });
		}
		when(request.getParameterMap()).thenReturn(map);
		return request;
	}

	private HttpServletRequest request(String name, String[] values) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put(name, values);
		when(request.getParameterMap()).thenReturn(map);
		return request;
	}

	private Map<Annotation, ActionInterceptor<Annotation>> noInterceptors() {
		return Collections.<Annotation, ActionInterceptor<Annotation>> emptyMap();
	}
}