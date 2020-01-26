package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;

import bg.fmi.spring.course.project.constants.RequestState;

public interface ChangeRoleRequestService {
    List<RequestState> getAllRequests();
    RequestState getRequestById(Long id);
    RequestState addRequest(RequestState requestState);
    RequestState updateRequest(Long id, RequestState requestState);
    RequestState acceptRequest(Long id);
    RequestState rejectRequest(Long id);
    RequestState deleteRquest(Long id);
}
