window.onload = function() {
    const source = new EventSource("/api/messages")
    source.addEventListener("message", function (event) {
        const data = event.data
        if (data === "Connected") {
            return
        }
        const json = JSON.parse(data)
        const name = json.name

        document.querySelector("#messages-div").innerHTML += `<p>${name}</p>`
    })
}