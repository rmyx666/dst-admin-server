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
    }
}
