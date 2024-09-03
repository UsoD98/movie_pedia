document.querySelector('form').addEventListener('submit', function(e) {
    e.preventDefault();

    fetch(this.action, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(response => {
        if (response.ok) {
            // 성공 시 현재 페이지로
            window.location.reload();
        } else {
            console.error('Follow/Unfollow failed');
        }
    });
});