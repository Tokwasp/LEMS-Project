package lems.cowshed.domain.user.query;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
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