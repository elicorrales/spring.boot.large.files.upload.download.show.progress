
var sessionId;
var submittedFileList = {}
var numberOfSubmittedFiles = 0;
var keepCheckingFileUploadStatus = false;
var timeout;

const messageElem      = document.getElementById('message');
const fileSelectorElem = document.getElementById('fileSelector');
const fileListElem     = document.getElementById('fileList');
const submitElem       = document.getElementById('submitBtn');

submitElem.disabled = true;

fileSelectorElem.addEventListener('change',() => {
	submittedFileList = {};
    displayFileList();
	numberOfSubmittedFiles = 0;
	clearMessage();
	let fseFiles = fileSelectorElem.files;
	for (i=0;i<fseFiles.length;i++) {
		submittedFileList[fseFiles[i].name] = fseFiles[i];
		numberOfSubmittedFiles++;
	}
	if (numberOfSubmittedFiles>0) submitElem.disabled = false;
	
	setFilesStatuses('Selected');
	displayFileList();
});


const clearMessage = () => {
	messageElem.innerHTML = '';
};

const displayMessage = (message) => {
	messageElem.innerHTML = '<h2>' + message + '</h2>';
};

const displayFileList = () => {
	if (submittedFileList==undefined) { return; }
	let html = '';
	for (key in submittedFileList) {
		let file = submittedFileList[key];
		let isFileUploaded = file.uploadedSize && file.uploadedSize==file.size?true:false;
		html += (isFileUploaded?'<tr style="color:white;background-color:green">':'<tr>')
				+ '<td>' + file.name + '</td>'
				+ '<td>' + (file.uploadedSize?file.uploadedSize:0) + '</td>'
				+ '<td>'+  file.size  + '</td>'
				+ '<td>' + file.status + '</td>'
			+ '</tr>';
	}
	fileListElem.innerHTML = html;
};

const setFilesStatuses = (status) => {
	if (submittedFileList==undefined) { return; }
	for (key in submittedFileList) {
		let file = submittedFileList[key];
		file.status = status;
	}
};

const updateFilesStatuses = (list) => {
	if (!submittedFileList || !list) { return; }
	let numberOfReceivedStatuses = 0;
	let numberOfMatches = 0;
	for (key in list) {
		if (submittedFileList[key]!==undefined) {
			submittedFileList[key].uploadedSize = list[key].size;
			submittedFileList[key].status = list[key].status;
			numberOfReceivedStatuses++;
			if (submittedFileList[key].size==list[key].size) {
				numberOfMatches++;
			}
		}
	}
    displayFileList();
	if (numberOfReceivedStatuses>=numberOfSubmittedFiles && numberOfMatches==numberOfSubmittedFiles) {
		keepCheckingFileUploadStatus = false;
		displayMessage('All Files Uploaded');
		submitElem.disabled = false;
		fileSelectorElem.disabled = false;
		setFilesStatuses('Uploaded');
        submittedFileList = {}
        numberOfSubmittedFiles = 0;
		submitElem.disabled = false;
		fileSelectorElem.files = null;
	}
};



const checkUploadStatus = () =>{

	if (!keepCheckingFileUploadStatus) {
		return false;
	};

	axios.get('/uploadprogress',{headers:{SESSIONID:sessionId}})
	.then(
		result => {
			if (result.data && result.data.fileStatuses) {
				updateFilesStatuses(result.data.fileStatuses);
			}
		}
	)
	.catch(
		error => {
			console.log(error);
		}
	);
	
	return true;
};

const fileUploadStatusRunner = () => {
	if (!checkUploadStatus()) {
		return;
	}
	setTimeout(()=>{
		fileUploadStatusRunner();
	},200);
};

const isSubmittedFileListEmpty = () => {
	for (var key in submittedFileList) {
		if (submittedFileList.hasOwnProperty(key)) return false;
	}
	return true;
};

const doSubmit = (event) => {

	submitElem.disabled = true;
	fileSelectorElem.disabled = true;
	
	clearMessage();
	event.preventDefault();

	if (!submittedFileList || isSubmittedFileListEmpty()) {
		displayMessage('No Files Selected');
		return;
	}

	console.log('submit file upload');

	console.log(sessionId);
	
	const formData = new FormData();
	for (key in submittedFileList) {
		let file = submittedFileList[key];
		formData.append('file',file);
	}

	setFilesStatuses('Submitted...');
	displayFileList();

	keepCheckingFileUploadStatus= true;
	axios({
		method:'POST',
		url:'/betterfileupload',
		timeout:1000*60*30, //30 minutes
		data:formData,
		headers:{ SESSIONID:sessionId }
	})
	.then( result => {console.log(result); })
	.catch( error => {
        console.log(error);
        displayMessage('Upload Timed Out');
        keepCheckingFileUploadStatus = false;
    });
	
	//we dont want this to fire off before the file submission
	//has a chance to start working, otherwise
	//it could come back as already done (if you previously
	//hit the submit button and uploaded the files,
	//and you hit it again, the server-side upload will
	//remove the pre-existing file first, but maybe
	//not in time to accurately reflect this in the status request
	setTimeout( fileUploadStatusRunner, 1000);

};

const doProceed = (result) => {
	console.log(result);
	let uuid = result.data?(result.data?result.data.uuid:result.data):result;
	console.log(uuid);
	sessionId = uuid;
};

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
};

initialize();