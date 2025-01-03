function loadMobiles() {
    fetch('http://localhost:8080/mobiles')
        .then(response => response.json())
        .then(mobiles => {
            let rows = mobiles.map(mobile => {
                let imageSrc = mobile.image ? `data:image/jpeg;base64,${mobile.image}` : 'http://localhost:8080/mobiles';
                return `
                    <tr>
                        <td>${mobile.id}</td>
                        <td>${mobile.brand}</td>
                        <td>${mobile.model}</td>
                        <td>${mobile.price}</td>
                        <td>${mobile.currency}</td>
                        <td>${mobile.stock}</td>
                        <td><img src="${imageSrc}" style="width:50px;" /></td>
                        <td>
                            <button onclick='editMobile(${JSON.stringify(mobile)})'>Edit</button>
                            <button onclick='deleteMobile(${mobile.id})'>Delete</button>
                        </td>
                    </tr>
                `;
            }).join('');
            document.getElementById('mobileTable').innerHTML = rows;
        }).catch(error => console.error('Error:', error));
}
