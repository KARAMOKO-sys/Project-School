import { TestBed } from '@angular/core/testing';

import { UserResearchService } from './user-research.service';

describe('UserResearchService', () => {
  let service: UserResearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserResearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
