import { createStore } from 'vuex'

export default createStore({
    state: {
        userId: null,
    },
    mutations: {
        setUserId(state, id) {
            state.userId = id;
        }
    },
    actions: {
        updateUserId({ commit }, id) {
            commit('setUserId', id);
        }
    },
    getters: {
        getUserId(state) {
            return state.userId;
        }
    }
});