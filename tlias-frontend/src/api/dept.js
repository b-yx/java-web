import request from './index'

// Login
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

// Department APIs
export function getDeptList() {
  return request({
    url: '/depts',
    method: 'get'
  })
}

export function deleteDept(id) {
  return request({
    url: '/depts',
    method: 'delete',
    params: { id }
  })
}

export function addDept(data) {
  return request({
    url: '/depts',
    method: 'post',
    data
  })
}

export function getDeptById(id) {
  return request({
    url: `/depts/${id}`,
    method: 'get'
  })
}

export function updateDept(data) {
  return request({
    url: '/depts',
    method: 'put',
    data
  })
}

// Employee APIs
export function getEmpList(params) {
  return request({
    url: '/emps',
    method: 'get',
    params
  })
}

export function addEmp(data) {
  return request({
    url: '/emps',
    method: 'post',
    data
  })
}

export function deleteEmps(ids) {
  return request({
    url: '/emps',
    method: 'delete',
    params: { ids: ids.join(',') }
  })
}

export function getEmpById(id) {
  return request({
    url: `/emps/${id}`,
    method: 'get'
  })
}

export function updateEmp(data) {
  return request({
    url: '/emps',
    method: 'put',
    data
  })
}

// Report APIs
export function getEmpJobData() {
  return request({
    url: '/report/empJobData',
    method: 'get'
  })
}

export function getEmpGenderData() {
  return request({
    url: '/report/empGenderData',
    method: 'get'
  })
}

export function getStudentDegreeData() {
  return request({
    url: '/report/studentDegreeData',
    method: 'get'
  })
}

export function getStudentCountData() {
  return request({
    url: '/report/studentCountData',
    method: 'get'
  })
}

// Upload
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData
  })
}

// ====== CLazz (Class) APIs ======
export function getClazzList(params) {
  return request({
    url: '/clazzs',
    method: 'get',
    params
  })
}

export function addClazz(data) {
  return request({
    url: '/clazzs',
    method: 'post',
    data
  })
}

export function getClazzById(id) {
  return request({
    url: `/clazzs/${id}`,
    method: 'get'
  })
}

export function updateClazz(data) {
  return request({
    url: '/clazzs',
    method: 'put',
    data
  })
}

export function deleteClazz(id) {
  return request({
    url: `/clazzs/${id}`,
    method: 'delete'
  })
}

export function getAllClazzs() {
  return request({
    url: '/clazzs/list',
    method: 'get'
  })
}

// ====== Student APIs ======
export function getStudentList(params) {
  return request({
    url: '/students',
    method: 'get',
    params
  })
}

export function addStudent(data) {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

export function getStudentById(id) {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

export function updateStudent(data) {
  return request({
    url: '/students',
    method: 'put',
    data
  })
}

export function deleteStudents(ids) {
  return request({
    url: `/students/${ids.join(',')}`,
    method: 'delete'
  })
}

export function violationStudent(id, score) {
  return request({
    url: `/students/violation/${id}/${score}`,
    method: 'put'
  })
}

// ====== Log APIs ======
export function getLogList(params) {
  return request({
    url: '/log/page',
    method: 'get',
    params
  })
}
