package lems.cowshed.domain.user.query;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.user.query.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;

class UserQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    EventRepository eventRepository;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    EventParticipantRepository eventParticipantRepository;

}