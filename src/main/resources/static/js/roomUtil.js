const RoomUtil = {
    roomId: null,
    goRoomIndex() {
        const dom = document.createElement('a')
        dom.href = '/'
        dom.click()
    },
    saveRoomId(roomId) {
        window.localStorage.setItem('roomId', roomId)
    },
    getRoomId() {
        const roomId = window.localStorage.getItem('roomId');
        if (roomId == null) {
            this.goRoomIndex()
            return
        }
        return roomId;
    },
    serverId: null,
    goServerIndex() {
        const dom = document.createElement('a')
        dom.href = '/'
        dom.click()
    },
    saveServerId(serverId) {
        window.localStorage.setItem('serverId', serverId)
    },
    getServerId() {
        const serverId = window.localStorage.getItem('serverId');
        if (serverId == null) {
            this.goServerIndex()
            return
        }
        return serverId;
    }
}
