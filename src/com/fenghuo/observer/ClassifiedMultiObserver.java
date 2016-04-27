package com.fenghuo.observer;

import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.fenghuo.handler.Handler;
import com.fenghuo.handler.ISender;
import com.fenghuo.handler.MessageObjects;
import com.fenghuo.observer.annotation.ObserverAction;
import com.fenghuo.observer.annotation.Param;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with Android Studio
 * <p>
 * Project: lockscreen
 * Author: zhangshaolin(www.iooly.com)
 * Date:   14-6-26
 * Time:   上午8:17
 * Email:  app@iooly.com
 */
public final class ClassifiedMultiObserver<C> implements ClassifiedObserver<C, Object> {

    private static final int WHAT_NOTIFY_ACTION = 0x19880816;

    private static final Object[] EMPTY_PARAMS = new Object[0];

    private final List<ObserverReference<C>> mListenerRefs = newObserverList();

    private final Map<C, List<ObserverReference<C>>> mClassifiedMap = new ConcurrentHashMap<>();

    private final ISender mCallbackSender;
    /**
     * 必须有一处引用， 不然会被释放
     */
    private final CallbackHandler mCallbackHandler = new CallbackHandler();

    public ClassifiedMultiObserver(Looper callbackLooper) {
        mCallbackSender = ISender.Factory.newSender(mCallbackHandler, callbackLooper);
    }

    public ClassifiedMultiObserver() {
        this(Looper.getMainLooper());
    }

    @Override
    public final void register(Object observer) {
        if (observer == null) {
            return;
        }
        checkAndRemoveUnusedObservers();
        addObserver(observer);
    }

    public final void unregister(Object observer) {
        if (observer == null) {
            return;
        }
        checkAndRemoveUnusedObservers();
        int len = mListenerRefs.size();
        for (int i = len - 1; i >= 0; i--) {
            WeakReference<Object> ref = mListenerRefs.get(i);
            if (observer == ref.get()) {
                mListenerRefs.remove(i);
                break;/**找到后终止 li 2015-01-23**/
            }
        }
    }

    public void notifyAll(String action, Object... params) {
        List<ObserverReference<C>> list = getList();
        for (ObserverReference<C> ref : list) {
            ref.performNotify(action, params);
        }
    }

    public void notifyClassify(C classify, String action, Object... params) {
        List<ObserverReference<C>> list = getList(classify);
        if (list != null) {
            for (ObserverReference<C> ref : list) {
                ref.performNotify(action, params);
            }
        }
    }

    @Override
    public void register(Object observer, C... classifies) {
        if (observer == null) {
            return;
        }

        if (classifies == null) {
            register(observer);
            return;
        }

        checkAndRemoveUnusedObservers();

        ObserverReference<C> ref = addObserver(observer);

        if (ref != null) {

            if (ref.classifies != null) {
                removeReferenceFromClassifyMap(ref);
            }

            ref.classifies = classifies;
            for (C classify : classifies) {
                List<ObserverReference<C>> list = mClassifiedMap.get(classify);
                if (list == null) {
                    list = newObserverList();
                    mClassifiedMap.put(classify, list);
                }
                list.add(ref);
            }
        }
    }

    private void removeReferenceFromClassifyMap(ObserverReference<C> ref) {
        if (ref != null && ref.classifies != null) {
            for (C classify : ref.classifies) {
                List<ObserverReference<C>> list = mClassifiedMap.get(classify);
                if (list != null) {
                    list.remove(ref);
                    if (list.isEmpty()) {
                        mClassifiedMap.remove(classify);
                    }
                } else {
                    mClassifiedMap.remove(classify);
                }
            }
        }
    }

    private ObserverReference<C> addObserver(Object observer) {
        ObserverReference<C> existsObserver = null;
        for (WeakReference<Object> ref : mListenerRefs) {
            if (observer == ref.get()) {
                existsObserver = (ObserverReference<C>) ref;
                break;
            }
        }

        if (existsObserver == null) {
            mListenerRefs.add(existsObserver = new ObserverReference<>(observer));
            existsObserver.sender = mCallbackSender;
        }

        return existsObserver;
    }

    private List<ObserverReference<C>> getList() {
        checkAndRemoveUnusedObservers();
        return mListenerRefs;
    }

    private List<ObserverReference<C>> getList(C classify) {
        checkAndRemoveUnusedObservers();
        return classify != null ? mClassifiedMap.get(classify) : null;
    }

    public final void checkAndRemoveUnusedObservers() {
        int len = mListenerRefs.size();
        for (int i = len - 1; i >= 0; i--) {
            ObserverReference<C> ref = mListenerRefs.get(i);
            if (ref == null || ref.get() == null) {
                removeReferenceFromClassifyMap(ref);
                mListenerRefs.remove(i);
            }
        }
    }

    private static <C> List<ObserverReference<C>> newObserverList() {
        return Collections.synchronizedList(new ArrayList<ObserverReference<C>>());
    }

    private class CallbackHandler implements Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != WHAT_NOTIFY_ACTION) {
                return;
            }
            if (msg.obj != null && (msg.obj instanceof MessageObjects)) {
                MessageObjects objects = (MessageObjects) msg.obj;
                try {
                    ObserverReference<C> ref = (ObserverReference<C>) objects.obj0;
                    String action = (String) objects.obj1;
                    Object[] params = (Object[]) objects.obj2;
                    if (params == null) {
                        params = EMPTY_PARAMS;
                    }
                    if (ref != null && action != null) {
                        ref.notify(action, params);
                    }
                } finally {
                    objects.recycle();
                }
            }

        }
    }

    private static class ObserverReference<C> extends WeakReference<Object> {

        public C[] classifies;
        public ISender sender;

        private final Map<String, Action> actionMap = new ConcurrentHashMap<>();

        public ObserverReference(Object r) {
            super(r);
            initActions(r.getClass());
        }

        public void performNotify(String action, Object... params) {
            Message msg = sender.obtainMessage(WHAT_NOTIFY_ACTION);
            if (msg != null) {
                MessageObjects objects = MessageObjects.obtain();
                objects.obj0 = this;
                objects.obj1 = action;
                objects.obj2 = params;
                msg.obj = objects;
                msg.sendToTarget();
            }
        }

        public void notify(String actionName, Object... params) {
            Action action = actionName == null ? null : actionMap.get(actionName);
            if (action != null) {
                Object target = get();
                if (target != null) {
                    try {
                        action.invoke(target, params);
                    } catch (InvocationTargetException e) {
                        Throwable targetException = e.getTargetException();
                        if (targetException == null) {
                            targetException = e.getCause();
                        }
                        if (targetException == null) {
                            targetException = e;
                        }
                        throw new NotifyInvokeException(targetException);
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        private void initActions(Class<?> clazz) {
            if (clazz == null) {
                return;
            }

            Method[] methods = clazz.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (Method method : methods) {
                    ObserverAction actionName = method.getAnnotation(ObserverAction.class);
                    if (actionName != null && actionName.value() != null) {
                        Annotation[][] annotations = method.getParameterAnnotations();
                        String[] actionNames = actionName.value();
                        if (annotations == null || annotations.length <= 0) {
                            Action action = new Action(method);
                            for (String an : actionNames) {
                                method.setAccessible(true);
                                actionMap.put(an, action);
                            }
                        } else {
                            int len = annotations.length;
                            Parameter[] parameters = new Parameter[len];
                            Class<?>[] types = method.getParameterTypes();

                            for (int i = 0; i < len; i++) {
                                Annotation[] sub = annotations[i];
                                Param param = null;
                                for (Annotation annotation : sub) {
                                    if (annotation instanceof Param) {
                                        param = (Param) annotation;
                                        break;
                                    }
                                }

                                if (param == null) {
                                    throw new InvalidParameterException("every params must has a Param annotation");
                                }

                                if (param.value() == null) {
                                    throw new NullPointerException("Param value can not be null");
                                }

                                parameters[i] = new Parameter(i, param.value(), types[i]);
                            }

                            Action action = new Action(method, parameters);
                            for (String an : actionNames) {
                                method.setAccessible(true);
                                actionMap.put(an, action);
                            }
                        }
                    }
                }
            }

            initActions(clazz.getSuperclass());
        }

    }

    private static class Action {
        final Method method;
        final int paramsCount;
        final Parameter[] parameters;
        final Map<String, Parameter> parameterMap;
        final Object[] values;

        Action(Method method, Parameter[] parameters) {
            this.method = method;
            this.paramsCount = parameters == null ? 0 : parameters.length;
            this.parameters = parameters;
            if (paramsCount > 0) {
                Map<String, Parameter> indexMap = new HashMap<>(paramsCount);
                for (Parameter parameter : parameters) {
                    indexMap.put(parameter.name, parameter);
                }
                values = new Object[paramsCount];
                parameterMap = indexMap;
            } else {
                values = null;
                parameterMap = null;
            }
        }

        Action(Method method) {
            this(method, null);
        }

        void invoke(@NonNull Object target, @NonNull Object... params) throws InvocationTargetException, IllegalAccessException {
            if (paramsCount <= 0) {
                method.invoke(target);
            } else {
                int len = params.length;
                if (len % 2 != 0) {
                    throw new RuntimeException("params count must be n*2");
                }

                resetValues();

                int count = len >> 1;
                int index;
                for (int i = 0; i < count; i++) {
                    index = i << 1;
                    String name = (String) params[index];
                    Object value = params[index + 1];
                    Parameter parameter = parameterMap.get(name);
                    if (parameter != null) {
                        values[parameter.index] = value;
                    }
                }

                method.invoke(target, values);
            }
        }

        void resetValues() {
            for (Parameter parameter : parameters) {
                values[parameter.index] = parameter.defaultValue;
            }
        }
    }

    private static class Parameter {

        final int index;
        final String name;
        final Class<?> type;
        final Object defaultValue;

        private Parameter(int index, String name, Class<?> type) {
            this.index = index;
            this.name = name;
            this.type = type;
            this.defaultValue = getDefaultValue(type);
        }

        private static Object getDefaultValue(Class<?> typeClass) {
            if (typeClass == int.class || typeClass == Integer.class
                    || typeClass == double.class || typeClass == Double.class
                    || typeClass == short.class || typeClass == Short.class
                    || typeClass == byte.class || typeClass == Byte.class
                    || typeClass == char.class || typeClass == Character.class
                    || typeClass == float.class || typeClass == Float.class
                    || typeClass == long.class || typeClass == Long.class) {
                return 0;
            } else if (typeClass == boolean.class || typeClass == Boolean.class) {
                return false;
            }
            return null;
        }
    }
}
