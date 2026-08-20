import axios from 'axios';

const BASE_URL = '/api';

export function addJob(jobInfo) {
    return axios.post(`${BASE_URL}/job/add`, jobInfo);
}

export function updateJob(jobInfo) {
    return axios.post(`${BASE_URL}/job/update`, jobInfo);
}

export function deleteJob(jobId) {
    return axios.post(`${BASE_URL}/job/delete`, { jobId });
}

export function triggerJob(jobId) {
    return axios.post(`${BASE_URL}/job/trigger`, { jobId });
}

export function pauseJob(jobId) {
    return axios.post(`${BASE_URL}/job/pause`, { jobId });
}

export function resumeJob(jobId) {
    return axios.post(`${BASE_URL}/job/resume`, { jobId });
}

export function listJobs(params) {
    return axios.get(`${BASE_URL}/job/list`, { params });
}

export function listJobLogs(params) {
    return axios.get(`${BASE_URL}/job/log/list`, { params });
}