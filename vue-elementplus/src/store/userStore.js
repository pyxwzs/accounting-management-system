import { defineStore } from "pinia";
import { ref } from "vue";
export const useUserStore = defineStore("userStoreId", ()=>{
  
  const token=ref()
  const user=ref()
  const setToken=(value)=>{
    token.value=value
  }
  const setUser=(value)=>{
    user.value=value
  }
  /**
   * 退出登录
   */
  const logout = () => {
    token.value=''
    user.value=''
  }
  return {
    token,
    user,
    logout,
    setToken,
    setUser
  }
},{
  persist: {
    paths: ['token', 'user']
  }
});