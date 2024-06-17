import { createStore } from 'vuex'

export default createStore({
    state: {
        userId: null,
        authority: null,
    },
    mutations: {
        setUserId(state, id) {
            state.userId = id;
        },
        setAuthority(state, authority) {
            state.authority = authority;
        }
    },
    actions: {
        updateUserId({ commit }, id) {
            commit('setUserId', id);
        },
        updateAuthority({ commit }, authority) {
            commit('setAuthority', authority);
        }
    },
    getters: {
        getUserId(state) {
            return state.userId;
        },
        getAuthority(state) {
            return state.authority;
        }
    }
});