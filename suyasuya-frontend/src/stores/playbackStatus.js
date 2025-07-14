import { defineStore } from "pinia";
import { ref } from "vue";

export const usePlaybackStatusStore = defineStore('playbackStatus', () => {
    const playbackVolume = ref(100)
    const playbackProgress = ref({})

    const setPlaybackVolume = volume => {
        playbackVolume.value = volume
    }

    const setPlaybackProgress = (videoId, progress) => {
        playbackProgress.value[videoId] = progress
    }
    const getPlaybackProgress = (videoId) => {
        return playbackProgress.value[videoId] || 0
    }

    return {
        playbackVolume,
        playbackProgress,
        setPlaybackVolume,
        setPlaybackProgress,
        getPlaybackProgress
    }
}, {
    persist: true
})