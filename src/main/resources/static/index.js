
var sessionId;
var fileList = [];
var keepCheckingUploadStatus = false;
var timeout;

const messageElem      = document.getElementById('message');
const fileSelectorElem = document.getElementById('fileSelector');
const fileListElem     = document.getElementById('fileList');

fileSelectorElem.addEventListener('change',() => {
	fileList = [];
	messageElem.innerHTML = '';
	let fseFiles = fileSelectorElem.files;
	for (i=0;i<fseFiles.length;i++) {
		fileList.push(fseFiles[i]);
	}
	setFilesStatuses('Selected');
	displayFileList();
});

fileSelectorElem.addEventListener('click',() => {
	fileList = [];
	messageElem.innerHTML = '';
	let fseFiles = fileSelectorElem.files;
	fileListElem.innerHTML = '';
});



const displayFileList = () => {
	if (fileList==undefined) { return; }
	let html = '';
	for (i=0;i<fileList.length;i++) {
		let file = fileList[i];
		html += '<tr><td>' + fileList[i].name + '</td><td>' + fileList[i].size + '</td><td>' + fileList[i].status + '</td></tr>';
	}
	fileListElem.innerHTML = html;
};

const setFilesStatuses = (status) => {
	if (fileList==undefined) { return; }
	for (i=0;i<fileList.length;i++) {
		fileList[i].status = status;
	}
};


const checkUploadStatus = () =>{

	if (!keepCheckingUploadStatus) {
		clearTimeout(timeout);
		return;
	};

	axios.get('/uploadprogress',{headers:{SESSIONID:sessionId}})
	.then(
		result => {
			console.log(result);
		}
	)
	.catch(
		error => {
			console.log(error);
		}
	);
};


const doSubmit = (form,obj,event) => {

	messageElem.innerHTML = '';
	event.preventDefault();

	if (!fileList || fileList.length<1) {
		messageElem.innerHTML = '<h2>No Files Selected</h1>';
		return;
	}

	console.log('submit file upload');

	console.log(sessionId);
	
	const formData = new FormData();
	fileList.forEach( f => { formData.append('file',f); });

	setFilesStatuses('Submitted...');
	displayFileList();

	keepCheckingUploadStatus= true;
	axios({
		method:'POST',
		url:'/betterfileupload',
		timeout:90000,
		data:formData,
		headers:{
			SESSIONID:sessionId
		}
	})
	.then(
		result => {
			console.log(result);
			keepCheckingUploadStatus = false;
		}
	)
	.catch(
		error => {
			console.log(error);
			keepCheckingUploadStatus = false;
		}
	);
	
	timeout = setTimeout(checkUploadStatus,200);
};

const doProceed = (result) => {
	console.log(result);
	let uuid = result.data?(result.data?result.data.uuid:result.data):result;
	console.log(uuid);
	sessionId = uuid;
}

const initialize = () => {
	axios.get('/sessionid')
		.then(
			result => {
				doProceed(result);
			}
		)
		.catch(
			error => {
				let theError = JSON.stringify(error.response?(error.response.data?error.response.data:error.response):error);
				console.log(theError);
				let body = document.getElementById('body');
				body.innerHTML = '<h1>There was an Error during initialization</h1>'
								+ '<hr>'
								+ theError;
			}
		)
}

initialize();