/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.shared;

import java.io.Serializable;
import java.util.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;

/**
 * FastMap.
 * 
 * @param <V>
 *            V
 * 
 */
public class FastMap<V> extends AbstractMap<String, V> implements Serializable {
	private static final long serialVersionUID = 1636720656907824444L;

	/**
	 * FastMapEntry.
	 * 
	 * @param <V>
	 *            V
	 * 
	 */
	private static class FastMapEntry<V> implements Map.Entry<String, V> {

		private final String key;
		private V value;

		FastMapEntry(final String aKey, final V aValue) {
			key = aKey;
			value = aValue;
		}

		@Override
		public boolean equals(final Object a) {
			if (a instanceof Map.Entry<?, ?>) {
				Map.Entry<?, ?> s = (Map.Entry<?, ?>) a;
				if (equalsWithNullCheck(key, s.getKey())
						&& equalsWithNullCheck(value, s.getValue())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			int keyHash = 0;
			int valueHash = 0;
			if (key != null) {
				keyHash = key.hashCode();
			}
			if (value != null) {
				valueHash = value.hashCode();
			}
			return keyHash ^ valueHash;
		}

		@Override
		public V setValue(final V object) {
			V old = value;
			value = object;
			return old;
		}

		private boolean equalsWithNullCheck(final Object obj1, final Object obj2) {
			return equalWithNull(obj1, obj2);
		}
	}

	private static boolean equalWithNull(final Object obj1, final Object obj2) {
		if (obj1 == obj2) {
			return true;
		} else if (obj1 == null) {
			return false;
		} else {
			return obj1.equals(obj2);
		}
	}

	/**
	 * JsMap.
	 * 
	 * @param <V>
	 *            V
	 * 
	 */
	private static class JsMap<V> extends JavaScriptObject {

		public static FastMap.JsMap<?> create() {
			return JavaScriptObject.createObject().cast();
		}

		protected JsMap() {
		}

		public final native boolean containsKey(final String key)/*-{
			return this.hasOwnProperty(key);
		}-*/;

		public final native V get(final String key) /*-{
			return this[key];
		}-*/;

		public final native List<String> keySet() /*-{
			var s = @java.util.ArrayList::new()();
			for ( var key in this) {
				if (!this.hasOwnProperty(key))
					continue;
				s.@java.util.ArrayList::add(Ljava/lang/Object;)(key);
			}
			return s;
		}-*/;

		public final native V put(final String key, final V value) /*-{
			var previous = this[key];
			this[key] = value;
			return previous;
		}-*/;

		public final native V remove(final String key) /*-{
			var previous = this[key];
			delete this[key];
			return previous;
		}-*/;

		public final native int size() /*-{
			var count = 0;
			for ( var key in this) {
				if (this.hasOwnProperty(key))
					++count;
			}
			return count;
		}-*/;

		public final native List<V> values() /*-{
			var s = @java.util.ArrayList::new()();
			for ( var key in this) {
				if (!this.hasOwnProperty(key))
					continue;
				s.@java.util.ArrayList::add(Ljava/lang/Object;)(this[key]);
			}
			return s;
		}-*/;
	}

	private transient HashMap<String, V> javaMap;
	private transient FastMap.JsMap<V> map;

	public FastMap() {
		if (GWT.isScript()) {
			map = JsMap.create().cast();
		} else {
			javaMap = new HashMap<String, V>();
		}
	}

	@Override
	public void clear() {
		if (GWT.isScript()) {
			map = JsMap.create().cast();
		} else {
			javaMap.clear();
		}
	}

	@Override
	public boolean containsKey(final Object key) {
		if (GWT.isScript()) {
			return map.containsKey(String.valueOf(key));
		} else {
			return javaMap.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(final Object value) {
		return values().contains(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
		if (GWT.isScript()) {
			return new AbstractSet<Map.Entry<String, V>>() {

				@Override
				public boolean contains(final Object key) {
					Map.Entry<?, ?> s = (Map.Entry<?, ?>) key;
					Object value = get(s.getKey());
					if (value == null) {
						return value == s.getValue();
					} else {
						return value.equals(s.getValue());
					}
				}

				@Override
				public Iterator<Map.Entry<String, V>> iterator() {

					Iterator<Map.Entry<String, V>> custom = new Iterator<Map.Entry<String, V>>() {
						private final Iterator<String> keys = keySet().iterator();

						@Override
						public boolean hasNext() {
							return keys.hasNext();
						}

						@Override
						public Map.Entry<String, V> next() {
							String key = keys.next();
							return new FastMapEntry<V>(key, get(key));
						}

						@Override
						public void remove() {
							keys.remove();
						}
					};
					return custom;
				}

				@Override
				public int size() {
					return FastMap.this.size();
				}

			};
		} else {
			return javaMap.entrySet();
		}
	}

	@Override
	public V get(final Object key) {
		if (GWT.isScript()) {
			return map.get(String.valueOf(key));
		} else {
			return javaMap.get(key);
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<String> keySet() {
		if (GWT.isScript()) {
			return new AbstractSet<String>() {
				@Override
				public boolean contains(final Object key) {
					return FastMap.this.containsKey(key);
				}

				@Override
				public Iterator<String> iterator() {
					return map.keySet().iterator();
				}

				@Override
				public int size() {
					return FastMap.this.size();
				}
			};
		} else {
			return javaMap.keySet();
		}
	}

	@Override
	public V put(final String key, final V value) {
		if (GWT.isScript()) {
			return map.put(key, value);
		} else {
			return javaMap.put(key, value);
		}
	}

	@Override
	public void putAll(final Map<? extends String, ? extends V> m) {
		if (GWT.isScript()) {
			for (String s : m.keySet()) {
				map.put(s, m.get(s));
			}
		} else {
			javaMap.putAll(m);
		}
	}

	@Override
	public V remove(final Object key) {
		if (GWT.isScript()) {
			return map.remove((String) key);
		} else {
			return javaMap.remove(key);
		}
	}

	@Override
	public int size() {
		if (GWT.isScript()) {
			return map.size();
		} else {
			return javaMap.size();
		}
	}

	@Override
	public Collection<V> values() {
		if (GWT.isScript()) {
			return map.values();
		} else {
			return javaMap.values();
		}
	}
}
