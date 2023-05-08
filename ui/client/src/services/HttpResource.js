import axios from "axios";

const isHandlerEnabled = (config = {}) => {
    return !(Object.prototype.hasOwnProperty.call(config,"handlerEnabled") && !config.handlerEnabled);
};

const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/',
    withCredentials: true,
    timeout: 100000
});

const requestHandler = request => {
    if (isHandlerEnabled(request)) {
        console.log("Request Interceptor", request);
    }
    return request;
};

const errorHandler = error => {
    if (isHandlerEnabled(error.config)) {
        console.log("Error Interceptor", error);

        if (error.response) {
            if (error.response.status === 401) {
                //performLogout();
            }
        }
    }
    return Promise.reject({ ...error });
};

const successHandler = response => {
    if (isHandlerEnabled(response.config)) {
        console.log("Response Interceptor", response);
    }
    return response;
};

instance.interceptors.request.use(request => requestHandler(request));

instance.interceptors.response.use(
    response => successHandler(response),
    error => errorHandler(error)
);

export default instance;