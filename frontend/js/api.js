// ✅ frontend/api.js（注意：没有 export，用 window.callApi 挂全局）

window.callApi = function(operationType, payload) {
    return fetch("http://localhost:3000/api/socket", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ operationType, payload })
    }).then(res => {
        if (!res.ok) throw new Error("网络请求失败：" + res.status);
        return res.json();
    });
};