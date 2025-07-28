import { defineStore } from "pinia";
import { ref } from "vue";

export const usePlaybackStatusStore = defineStore('playbackStatus', () => {
    const playbackVolume = ref<number>(100)

    const setPlaybackVolume = (volume: number) => {
        playbackVolume.value = volume
    }

    return {
        playbackVolume,
        setPlaybackVolume,
    }
}, {
    persist: true
})