window.onload = function () {

    // browser 是否支援 SSE
    console.info(!!window.EventSource);

    // 建立 SSE 連線
    const eventSource = new EventSource('http://localhost:8081/sse/subscribe?id=rex');

    eventSource.onopen = function (event) {
        // 開啟 SSE 連線
        console.info("onopen");
        let text = document.getElementById('result').innerText;
        text += '\n 開啟：';
        document.getElementById('result').innerText = text;
    };

    eventSource.onmessage = function (event) {
        // 接受推播
        console.info("onmessage");
        console.info(event.data);
        console.info(event.origin);
        console.info(event.lastEventId);
        let text = document.getElementById('result').innerText;
        text += '\n' + event.data;
        document.getElementById('result').innerText = text;
    };

};