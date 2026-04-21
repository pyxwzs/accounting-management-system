import axios from "axios";
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/userStore'


const instance = axios.create({
  baseURL:'/api',
  headers: { "Content-Type": "application/json" },
  timeout: 60000,
});
// 请求拦截器
instance.interceptors.request.use((request)=>{
    const userStore = useUserStore()
    if(userStore.token){
        request.headers.Authorization=userStore.token
    }
    return request
},(error)=>{
    ElMessage.error(error.message)
    return Promise.reject(error.message);
})
// 响应拦截器
instance.interceptors.response.use((response)=>{
    const userStore = useUserStore()
    if(response.status===200){
        if(response.data.code===0){
            return response.data
        }
        ElMessage.error(response.data.message)
        if(response.data.code===401000){
            userStore.setToken(null);
            userStore.setUser(null);
            location.reload();
        }
    }
    return Promise.reject(response.statusText);
},(error)=>{
    ElMessage.error(error.message)
    return Promise.reject(error.message);
})

export default instance;