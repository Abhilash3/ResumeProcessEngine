const React = require('react');
const ResumeList = require('./resumeList');
const client = require('./../api/client');

class ResumeSearch extends React.Component {

    constructor(props) {
        super(props);
        this.state = {resumes: []};
        this.details = {};
        this.onScroll = this.onScroll.bind(this);
        this.onSearch = this.onSearch.bind(this);
    }

    componentWillMount() {
        document.addEventListener('scroll', this.onScroll, false);
    }

    componentWillUnmount() {
        document.removeEventListener('scroll', this.onScroll, false);
    }

    onSearch(e) {
        e.preventDefault();
        this.setState({resumes: []});

        var keywords = document.querySelector('#keywords').value.split(',')
            .map(a => a.trim()).filter(a => a.length);

        var experience = document.querySelector('#exp').value || 0;
        var sort = document.querySelector('#sort').value;

        this.retrieveResumes({keywords, experience, sort, page: 0});
    }

    retrieveResumes(query, resumes = []) {
        this.details.pending = true;
        client({
            method: 'POST',
            entity: query,
            path: '/resume/search',
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.setState({resumes: [...resumes, ...response.entity]});
            query.page = query.page + 1;
            this.details = {query, pending: false, count: response.entity.length};
            document.dispatchEvent(new Event('scroll'));
        }, response => {
            this.details.pending = false;
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {imp: true, type: 'danger', text: 'Search failed'}}));
        });
    }

    onScroll() {
        var scrollTop = document.documentElement && document.documentElement.scrollTop || document.body.scrollTop;
        var scrollHeight = document.documentElement && document.documentElement.scrollHeight || document.body.scrollHeight;
        var clientHeight = document.documentElement.clientHeight || window.innerHeight;

        if (Math.ceil(scrollTop + clientHeight) >= scrollHeight - 100 && this.details.count && !this.details.pending) {
            this.retrieveResumes(this.details.query, this.state.resumes);
        }
    }

    render() {
        return (
            <div>
                <div className='input-group'>
                    <div className='input-group-append'>
                        <span className='input-group-text'>Total</span>
                    </div>
                    <input type='number' className='form-control' min='0' placeholder='Experience...' id='exp'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>years of experience with keywords</span>
                    </div>
                    <input type='text' className='form-control' placeholder='Keywords...' id='keywords'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>sorted by</span>
                    </div>
                    <select className='custom-select' id='sort'>
                        <option value='relevance'>Relevance</option>
                        <option value='experience'>Experience</option>
                    </select>
                    <div className='input-group-append'>
                        <button className='btn btn-light' type='button' onClick={this.onSearch}>Search...</button>
                    </div>
                </div>
                <ResumeList resumes={this.state.resumes}/>
            </div>
        );
    }
}

module.exports = ResumeSearch;
