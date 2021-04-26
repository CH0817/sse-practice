window.onload = function () {
    // 建立 SSE 連線
    const eventSource = new EventSource('http://localhost:8080/sse/subscribe?id=rex');

    eventSource.onmessage = function (event) {
        // 接受推播
        let text = document.getElementById('result').innerText;
        text += '\n' + event.data;
        document.getElementById('result').innerText = text;
    };

    eventSource.onopen = function (event) {
        // 開啟 SSE 連線
        let text = document.getElementById('result').innerText;
        text += '\n 開啟：';
        document.getElementById('result').innerText = text;
    };

};